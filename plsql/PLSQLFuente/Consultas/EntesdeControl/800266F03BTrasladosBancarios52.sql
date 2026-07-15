SELECT  BANCO_ORIGEN.BANCO_ORIGEN                             "BANCO ORIGEN", 
        BANCO_ORIGEN.NUMEROCUENTA_ORIGEN                      "NUMERO CUENTA BANCARIA" ,
        BANCO_ORIGEN.FUENTE_FINANCIACION_ORIGEN               "FUENTE DE FINANCIACION",
        SUM(D.VALOR_DEBITO)                                   "VALOR TRASLADO",
        BANCO_DESTINO.BANCO_RECEPTOR                          "BANCO DESTINO",                        
        BANCO_DESTINO.NUMEROCUENTA_RECEPTOR                   "NUMERO CUENTA BANCARIA",
        BANCO_DESTINO.FUENTE_FINANCIACION_RECEPTOR            "FUENTE DE FINANCIACION"               
    FROM DETALLE_COMPROBANTE_CNT D 
     INNER JOIN  PLAN_CONTABLE P 
         ON  D.COMPANIA=P.COMPANIA
         AND D.ANO     =P.ANO 
         AND D.CUENTA  =P.CODIGO    
     LEFT JOIN (SELECT DISTINCT D.COMPANIA,
                                D.ANO,
                                D.TIPO_CPTE,
                                D.COMPROBANTE,
                                CASE WHEN VALOR_DEBITO>0
                                        THEN CB.CODBANCO_SIA
                                         END              BANCO_RECEPTOR,
                                CASE WHEN VALOR_DEBITO>0
                                        THEN CB.CUENTANUMERO
                                        END                     NUMEROCUENTA_RECEPTOR,
                                CASE WHEN VALOR_DEBITO>0
                                          THEN FR.CODIGO_SIA
                                          END                       FUENTE_FINANCIACION_RECEPTOR
                                
                   FROM DETALLE_COMPROBANTE_CNT D 
                     INNER JOIN  PLAN_CONTABLE P 
                         ON  D.COMPANIA=P.COMPANIA
                         AND D.ANO     =P.ANO 
                         AND D.CUENTA  =P.CODIGO    
                     INNER JOIN CUENTABANCOS CB
                         ON  D.COMPANIA=CB.COMPANIA
                         AND D.ANO     =CB.ANO
                         AND D.CUENTA  =CB.IDCONTABLE
                    INNER JOIN FUENTE_RECURSOS FR
                         ON  CB.COMPANIA  =FR.COMPANIA
                         AND CB.ANO       =FR.ANO
                         AND CB.RECURSOS  =FR.CODIGO
                WHERE D.COMPANIA=s$compania$s
                    AND D.ANO=s$ano$s 
                    AND D.TIPO_CPTE IN ('NBA','TRB','TRA')
                    AND P.CLASECUENTA IN('B')
                    AND D.VALOR_DEBITO>0) BANCO_DESTINO
         ON   D.COMPANIA  =BANCO_DESTINO.COMPANIA
        AND   D.ANO        =BANCO_DESTINO.ANO
        AND   D.TIPO_CPTE  =BANCO_DESTINO.TIPO_CPTE
        AND   D.COMPROBANTE=BANCO_DESTINO.COMPROBANTE       
       LEFT JOIN (SELECT DISTINCT D.COMPANIA,
                                  D.ANO,
                                  D.TIPO_CPTE,
                                  D.COMPROBANTE,
                                  CASE WHEN VALOR_CREDITO>0
                                        THEN CB.CODBANCO_SIA
                                         END                  BANCO_ORIGEN,
                                  CASE WHEN VALOR_CREDITO>0
                                         THEN CB.CUENTANUMERO
                                         END                  NUMEROCUENTA_ORIGEN,
                                  CASE WHEN VALOR_CREDITO>0
                                    THEN FR.CODIGO_SIA
                                    END                       FUENTE_FINANCIACION_ORIGEN  
                             FROM DETALLE_COMPROBANTE_CNT D 
                               INNER JOIN  PLAN_CONTABLE P 
                                   ON  D.COMPANIA=P.COMPANIA
                                   AND D.ANO     =P.ANO 
                                   AND D.CUENTA  =P.CODIGO    
                               INNER JOIN CUENTABANCOS CB
                                   ON  D.COMPANIA=CB.COMPANIA
                                   AND D.ANO     =CB.ANO
                                   AND D.CUENTA  =CB.IDCONTABLE
                              INNER JOIN FUENTE_RECURSOS FR
                                   ON  CB.COMPANIA  =FR.COMPANIA
                                   AND CB.ANO       =FR.ANO
                                   AND CB.RECURSOS  =FR.CODIGO
                          WHERE D.COMPANIA=s$compania$s
                              AND D.ANO=s$ano$s 
                              AND D.TIPO_CPTE IN ('NBA','TRB','TRA')
                              AND P.CLASECUENTA IN('B')
                              AND D.VALOR_CREDITO>0)BANCO_ORIGEN
        ON    D.COMPANIA    =BANCO_ORIGEN.COMPANIA
        AND   D.ANO        =BANCO_ORIGEN.ANO
        AND   D.TIPO_CPTE  =BANCO_ORIGEN.TIPO_CPTE
        AND   D.COMPROBANTE=BANCO_ORIGEN.COMPROBANTE  

 WHERE D.COMPANIA=s$compania$s
    AND D.ANO=s$ano$s 
    AND D.TIPO_CPTE IN ('NBA','TRB','TRA')
    AND P.CLASECUENTA IN('B')
    AND D.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
  GROUP BY D.COMPANIA,
      D.ANO,
      D.TIPO_CPTE,
      D.COMPROBANTE,
      BANCO_DESTINO.BANCO_RECEPTOR,
      BANCO_DESTINO.NUMEROCUENTA_RECEPTOR,
      BANCO_DESTINO.FUENTE_FINANCIACION_RECEPTOR,
      BANCO_ORIGEN.BANCO_ORIGEN,
      BANCO_ORIGEN.NUMEROCUENTA_ORIGEN,
      BANCO_ORIGEN.FUENTE_FINANCIACION_ORIGEN
     HAVING SUM(D.VALOR_DEBITO) >0 AND  SUM(D.VALOR_CREDITO)>0
