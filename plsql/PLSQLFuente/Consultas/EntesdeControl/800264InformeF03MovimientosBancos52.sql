SELECT P.CODIGO                                                                             "IDCONTABLE", 
       P.CODBANCO_SIA||' '||BN.NOMBREBANCO                                                  "BANCO ENTIDAD FINANCIERA",
       B.CUENTANUMERO                                                                       "CUENTA NO",
       P.NOMBRE                                                                              DENOMINACION,  
       F.NOMBRE                                                                             "FUENTE DE FINANCIACION",
       PCK_SYSMAN_UTL.FC_NOMBRE_MES(MES.NUMERO)                                             "MES REPORTADO",
       (CASE WHEN 1  = (MES.NUMERO)  THEN P.SALDO0  ELSE 0 END
                   + CASE WHEN 2  = (MES.NUMERO) THEN P.SALDO1  ELSE 0 END
                   + CASE WHEN 3  = (MES.NUMERO) THEN P.SALDO2  ELSE 0 END
                   + CASE WHEN 4  = (MES.NUMERO) THEN P.SALDO3  ELSE 0 END
                   + CASE WHEN 5  = (MES.NUMERO) THEN P.SALDO4  ELSE 0 END
                   + CASE WHEN 6  = (MES.NUMERO) THEN P.SALDO5  ELSE 0 END
                   + CASE WHEN 7  = (MES.NUMERO) THEN P.SALDO6  ELSE 0 END
                   + CASE WHEN 8  = (MES.NUMERO) THEN P.SALDO7  ELSE 0 END
                   + CASE WHEN 9  = (MES.NUMERO) THEN P.SALDO8  ELSE 0 END
                   + CASE WHEN 10 = (MES.NUMERO) THEN P.SALDO9 ELSE 0 END
                   + CASE WHEN 11 = (MES.NUMERO) THEN P.SALDO10 ELSE 0 END
                   + CASE WHEN 12 = (MES.NUMERO) THEN P.SALDO11 ELSE 0 END)               "SALDO INICIAL LIBROS MES",
                ( CASE WHEN 1  = (MES.NUMERO)    THEN P.AJUSTE0  ELSE 0 END
                   + CASE WHEN 2  = (MES.NUMERO) THEN P.AJUSTE1  ELSE 0 END
                   + CASE WHEN 3  = (MES.NUMERO) THEN P.AJUSTE2  ELSE 0 END
                   + CASE WHEN 4  = (MES.NUMERO) THEN P.AJUSTE3  ELSE 0 END
                   + CASE WHEN 5  = (MES.NUMERO) THEN P.AJUSTE4  ELSE 0 END
                   + CASE WHEN 6  = (MES.NUMERO) THEN P.AJUSTE5  ELSE 0 END
                   + CASE WHEN 7  = (MES.NUMERO) THEN P.AJUSTE6  ELSE 0 END
                   + CASE WHEN 8  = (MES.NUMERO) THEN P.AJUSTE7  ELSE 0 END
                   + CASE WHEN 9  = (MES.NUMERO) THEN P.AJUSTE8  ELSE 0 END
                   + CASE WHEN 10 = (MES.NUMERO) THEN P.AJUSTE9 ELSE 0 END
                   + CASE WHEN 11 = (MES.NUMERO) THEN P.AJUSTE10 ELSE 0 END
                   + CASE WHEN 12 = (MES.NUMERO) THEN P.AJUSTE11 ELSE 0 END)                 "SALDO INICIAL EXTRACTO MES",
       NVL(MOVIMIENTOS.INGRESOS,0)                                                             INGRESOS,
       NVL(MOVIMIENTOS.EGRESOS,0)                                                              EGRESOS,
       NVL(MOVIMIENTOS.NOTAS_DEBITO,0)                                                         "NOTAS DEBITO",
       NVL( MOVIMIENTOS.NOTAS_CREDITO,0)                                                       "NOTAS CREDITO",
       (CASE WHEN 1  = (MES.NUMERO)  THEN P.SALDO1  ELSE 0 END
                   + CASE WHEN 2  = (MES.NUMERO) THEN P.SALDO2  ELSE 0 END
                   + CASE WHEN 3  = (MES.NUMERO) THEN P.SALDO3  ELSE 0 END
                   + CASE WHEN 4  = (MES.NUMERO) THEN P.SALDO4  ELSE 0 END
                   + CASE WHEN 5  = (MES.NUMERO)  THEN P.SALDO5  ELSE 0 END
                   + CASE WHEN 6  = (MES.NUMERO) THEN P.SALDO6  ELSE 0 END
                   + CASE WHEN 7  = (MES.NUMERO) THEN P.SALDO7  ELSE 0 END
                   + CASE WHEN 8  = (MES.NUMERO)  THEN P.SALDO8  ELSE 0 END
                   + CASE WHEN 9  = (MES.NUMERO)  THEN P.SALDO9  ELSE 0 END
                   + CASE WHEN 10 = (MES.NUMERO)  THEN P.SALDO10 ELSE 0 END
                   + CASE WHEN 11 = (MES.NUMERO)  THEN P.SALDO11 ELSE 0 END
                   + CASE WHEN 12 = (MES.NUMERO)  THEN P.SALDO12 ELSE 0 END)                    "SALDO FINAL LIBROS",
       ( CASE WHEN 1  = (MES.NUMERO)    THEN P.AJUSTE1  ELSE 0 END
                   + CASE WHEN 2  = (MES.NUMERO) THEN P.AJUSTE2  ELSE 0 END
                   + CASE WHEN 3  = (MES.NUMERO) THEN P.AJUSTE3  ELSE 0 END
                   + CASE WHEN 4  = (MES.NUMERO) THEN P.AJUSTE4  ELSE 0 END
                   + CASE WHEN 5  = (MES.NUMERO) THEN P.AJUSTE5  ELSE 0 END
                   + CASE WHEN 6  = (MES.NUMERO) THEN P.AJUSTE6  ELSE 0 END
                   + CASE WHEN 7  = (MES.NUMERO) THEN P.AJUSTE7  ELSE 0 END
                   + CASE WHEN 8  = (MES.NUMERO) THEN P.AJUSTE8  ELSE 0 END
                   + CASE WHEN 9  = (MES.NUMERO) THEN P.AJUSTE9  ELSE 0 END
                   + CASE WHEN 10 = (MES.NUMERO) THEN P.AJUSTE10 ELSE 0 END
                   + CASE WHEN 11 = (MES.NUMERO) THEN P.AJUSTE11 ELSE 0 END
                   + CASE WHEN 12 = (MES.NUMERO) THEN P.AJUSTE12 ELSE 0 END)                         "SALDO FINAL EXTRACTO BANCARIO",
        ( CASE WHEN 1  = (MES.NUMERO)    THEN P.SALDO1-P.AJUSTE1  ELSE 0 END
                   + CASE WHEN 2  = (MES.NUMERO) THEN P.SALDO2-P.AJUSTE2  ELSE 0 END
                   + CASE WHEN 3  = (MES.NUMERO) THEN P.SALDO3-P.AJUSTE3  ELSE 0 END
                   + CASE WHEN 4  = (MES.NUMERO) THEN P.SALDO4-P.AJUSTE4  ELSE 0 END
                   + CASE WHEN 5  = (MES.NUMERO) THEN P.SALDO5-P.AJUSTE5  ELSE 0 END
                   + CASE WHEN 6  = (MES.NUMERO) THEN P.SALDO6-P.AJUSTE6  ELSE 0 END
                   + CASE WHEN 7  = (MES.NUMERO) THEN P.SALDO7-P.AJUSTE7  ELSE 0 END
                   + CASE WHEN 8  = (MES.NUMERO) THEN P.SALDO8-P.AJUSTE8  ELSE 0 END
                   + CASE WHEN 9  = (MES.NUMERO) THEN P.SALDO9-P.AJUSTE9  ELSE 0 END
                   + CASE WHEN 10 = (MES.NUMERO) THEN P.SALDO10-P.AJUSTE10 ELSE 0 END
                   + CASE WHEN 11 = (MES.NUMERO) THEN P.SALDO11-P.AJUSTE11 ELSE 0 END
                   + CASE WHEN 12 = (MES.NUMERO) THEN P.SALDO12-P.AJUSTE12 ELSE 0 END)               "SALDO CONCILIADO O AJUSTADO"    
   
FROM  PLAN_CONTABLE P 
  LEFT JOIN CUENTABANCOS B
    ON P.COMPANIA=B.COMPANIA
    AND P.ANO=B.ANO
    AND P.CODIGO=B.IDCONTABLE
  LEFT JOIN BANCO BN
    ON B.COMPANIA=BN.COMPANIA
    AND B.BANCO=BN.BANCO
  LEFT JOIN   FUENTE_RECURSOS F
    ON  B.COMPANIA=F.COMPANIA
    AND B.ANO=F.ANO
    AND B.RECURSOS=F.CODIGO
 INNER JOIN MES
       ON P.COMPANIA=MES.COMPANIA
       AND P.ANO=MES.ANO
  LEFT JOIN (SELECT D.COMPANIA,
                          D.ANO,
                          D.CUENTA CODIGO,
                          D.MES,
	   SUM(CASE WHEN T.CLASE_CONTABLE In ('I','A','S','E')
                                  THEN VALOR_DEBITO
                                  ELSE 0 END ) INGRESOS,
                         SUM(CASE WHEN T.CLASE_CONTABLE In ('E','A','D','S')
                                  THEN VALOR_CREDITO
                                  ELSE 0 END ) EGRESOS,
                         SUM(CASE WHEN T.CLASE_CONTABLE In ('B','C','G')
                                  THEN VALOR_DEBITO
                                  ELSE 0 END ) NOTAS_CREDITO,
                         SUM(CASE WHEN T.CLASE_CONTABLE In ('B','C','G')
                                  THEN VALOR_CREDITO
                                  ELSE 0 END ) NOTAS_DEBITO 
                    FROM  DETALLE_COMPROBANTE_CNT D INNER JOIN TIPO_COMPROBANTE T
                             ON  D.COMPANIA=T.COMPANIA
                             AND D.TIPO_CPTE=T.CODIGO
                         INNER JOIN PLAN_CONTABLE P 
                             ON D.COMPANIA=P.COMPANIA
                             AND D.ANO=P.ANO
                             AND D.CUENTA=P.CODIGO
                     
                    WHERE D.COMPANIA=s$compania$s
                         AND D.ANO=s$ano$s
                         AND  P.CLASECUENTA IN ('B')
                         AND T.CLASE_CONTABLE In ('I','A','S','E','D','G','B','C','G') 
                         AND MES BETWEEN s$mesInicial$s AND s$mesFinal$s
                    GROUP BY D.COMPANIA,
                          D.ANO,
                          D.CUENTA,
                          D.MES) MOVIMIENTOS
                          
           ON  P.COMPANIA=MOVIMIENTOS.COMPANIA
           AND P.ANO=MOVIMIENTOS.ANO
           AND P.CODIGO=MOVIMIENTOS.CODIGO
           AND MES.NUMERO=MOVIMIENTOS.MES
WHERE     P.COMPANIA=s$compania$s
      AND P.ANO=s$ano$s
      AND P.CLASECUENTA IN ('B')
      AND P.MOVIMIENTO NOT IN (0)
       AND MES.NUMERO BETWEEN s$mesInicial$s AND s$mesFinal$s
     AND  (P.SALDO0  + P.AJUSTE0     +     NVL(MOVIMIENTOS.INGRESOS,0) 
              +   NVL(MOVIMIENTOS.EGRESOS,0)+ NVL(MOVIMIENTOS.NOTAS_DEBITO,0)    +NVL( MOVIMIENTOS.NOTAS_CREDITO,0)             
               +   P.SALDO12	+       P.AJUSTE12     +  P.AJUSTE12) >0
ORDER BY MES.NUMERO,P.CODIGO
