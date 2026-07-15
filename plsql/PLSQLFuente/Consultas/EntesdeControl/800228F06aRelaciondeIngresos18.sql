SELECT CODIGO_SIA||D.CUENTA                       "Codigo Presupuestal",  
       TO_CHAR(D.FECHA,'DD/MM/YYYY')              "Fecha De Recaudo" , 
       D.TIPO|| '-'||D.COMPROBANTE                "Numero De Recibo",
       D.TERCERO_DET_NOM                          "Recibido De",
       D.DESCRIPCION_DET||TEXTO                   "Concepto Recaudo",
       (D.VALOR_CREDITO-D.VALOR_DEBITO)           "Valor",
      COMPROBANTES_CNT.NUMEROCUENTA               "Cuenta Bancaria",
      COMPROBANTES_CNT.CODBANCO_SIA
       
FROM V_DETALLE_AUXILIAR_PPTAL D INNER JOIN TIPO_COMPROBPP T
           ON  D.COMPANIA=T.COMPANIA
           AND D.TIPO=T.CODIGO
       LEFT JOIN (SELECT DISTINCT B.NOMBREBANCO||'-'||C.CUENTANUMERO NUMEROCUENTA,
                                  CN.COMPANIA,
                                  CN.ANO,
                                  CN.TIPO_CPTE,
                                  CN.COMPROBANTE, 
                                  C.CODBANCO_SIA
                      FROM DETALLE_COMPROBANTE_CNT CN INNER JOIN CUENTABANCOS C
                            ON CN.COMPANIA=C.COMPANIA
                            AND CN.ANO=C.ANO
                            AND CN.CUENTA=C.IDCONTABLE
                      INNER JOIN BANCO B
                             ON C.COMPANIA=B.COMPANIA
                            AND C.BANCO=B.BANCO                           
                     INNER JOIN PLAN_CONTABLE P
                             ON  P.COMPANIA=CN.COMPANIA
                             AND P.ANO=CN.ANO
                             AND P.CODIGO=CN.CUENTA
                      INNER JOIN TIPO_COMPROBANTE T
                             ON  CN.COMPANIA=T.COMPANIA
                              AND CN.TIPO_CPTE=T.CODIGO
                     WHERE    CN.COMPANIA=s$compania$s
                             AND CN.ANO=s$ano$s 
                             AND P.CLASECUENTA IN ('B')
                             AND CN.MES=s$mesFinal$s
                             AND T.CLASE_CONTABLE IN('I','B','S','J'))COMPROBANTES_CNT
  
          ON    D.COMPANIA=COMPROBANTES_CNT.COMPANIA
                AND D.ANO=COMPROBANTES_CNT.ANO
                AND D.TIPO=COMPROBANTES_CNT.TIPO_CPTE
                AND D.COMPROBANTE=COMPROBANTES_CNT.COMPROBANTE            
  LEFT JOIN  PLAN_PPTAL_CONFIG PC
          ON  D.COMPANIA=PC.COMPANIA
                AND D.ANO=PC.ANO
                AND D.CUENTA=PC.CODIGO
                AND D.CENTRO_COSTO=PC.CENTRO_COSTO
                AND D.AUXILIAR=PC.AUXILIAR
                AND D.FUENTE_RECURSOS=PC.FUENTE_RECURSO
  WHERE D.COMPANIA=s$compania$s
   AND D.ANO=s$ano$s 
   AND D.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
   AND T.CLASE IN ('ING','DIN','AIN')
