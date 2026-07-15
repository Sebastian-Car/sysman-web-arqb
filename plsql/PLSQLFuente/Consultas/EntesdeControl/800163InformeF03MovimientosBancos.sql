MERGE INTO CONSULTAS FIN USING (SELECT '800163InformeF03MovimientosBancos' INFORME ,TO_CLOB(q'[SELECT P.CODIGO                                     "IDCONTABLE", 
       P.CODBANCO_SIA        "BANCO ENTIDAD FINANCIERA",
       B.CUENTANUMERO                               "CUENTA NO",
       P.NOMBRE                                         DENOMINACION,  
       F.CODIGO_SIA                                 "FUENTE DE FINANCIACION",
       PCK_SYSMAN_UTL.FC_NOMBRE_MES(s$mesFinal$s)        "MES REPORTADO",
       P.SALDOs$mesInicial-1$s                       "SALDO INICIAL LIBROS MES",
       P.AJUSTEs$mesInicial-1$s                      "SALDO INICIAL EXTRACTO MES",
       NVL(MOVIMIENTOS.INGRESOS,0)        INGRESOS,
       NVL(MOVIMIENTOS.EGRESOS,0)                          EGRESOS,
       NVL(MOVIMIENTOS.NOTAS_DEBITO,0)                     "NOTAS DEBITO",
      NVL( MOVIMIENTOS.NOTAS_CREDITO,0)                    "NOTAS CREDITO",
       P.SALDOs$mesFinal$s                   "SALDO FINAL LIBROS",
       P.AJUSTEs$mesFinal$s                         "SALDO FINAL EXTRACTO BANCARIO",
       (P.SALDOs$mesFinal$s -P.AJUSTEs$mesFinal$s)                        "SALDO CONCILIADO O AJUSTADO"
       
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
  LEFT JOIN (SELECT D.COMPANIA,
                          D.ANO,
                          D.CUENTA CODIGO,
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
                    GROUP BY D.COMPANIA,]') || TO_CLOB(q'[
                          D.ANO,
                          D.CUENTA) MOVIMIENTOS
           ON  P.COMPANIA=MOVIMIENTOS.COMPANIA
           AND P.ANO=MOVIMIENTOS.ANO
           AND P.CODIGO=MOVIMIENTOS.CODIGO
WHERE     P.COMPANIA=s$compania$s
      AND P.ANO=s$ano$s
      AND P.CLASECUENTA IN ('B')
     AND  (P.SALDOs$mesInicial-1$s  + P.AJUSTEs$mesInicial-1$s     +     NVL(MOVIMIENTOS.INGRESOS,0) 
              +   NVL(MOVIMIENTOS.EGRESOS,0)+ NVL(MOVIMIENTOS.NOTAS_DEBITO,0)    +NVL( MOVIMIENTOS.NOTAS_CREDITO,0)             
               +   P.SALDOs$mesFinal$s  +       P.AJUSTEs$mesFinal$s     +  P.AJUSTEs$mesFinal$s) >0]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);