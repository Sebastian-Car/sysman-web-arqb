SELECT TO_CHAR(DC.FECHA,'DD/MM/YYYY')                                          FECHA_DE_PAGO,
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE                                       NO_DE_COMPROBANTE,
      DC.TERCERO_COM_NOM                                                        BENEFICIARIO,
       DC.TERCERO_COM                                                          CEDULA_NIT,
       DC.DESCRIPCION|| DC.TEXTO                                               DETALLE_DE_PAGO,
       DC.VLR_DOCUMENTO                                                        VALOR_COMPROBANTE_DE_PAGO,
       NVL(DESCUENTO,0)                                                         DESCUENTOS,
       VALOR_CREDITO                                                           NETO_PAGADO, 
       B.BANCO||'-'||B.NOMBREBANCO                                             BANCO,
       CB.CUENTANUMERO                                                         NO_CUENTA,
       NVL(DC.NRO_DOCUMENTO,'ND')                                               NO_CHEQUE 
FROM V_DETALLE_AUXILIAR_CNT DC 
  LEFT JOIN COMPROBANTE_PPTAL  CP
    ON DC.COMPANIA=CP.COMPANIA
    AND DC.ANO=CP.ANO
    AND DC.TIPO_CPTE=CP.TIPO
    AND DC.COMPROBANTE=CP.NUMERO
  INNER JOIN CUENTABANCOS CB
     ON DC.COMPANIA=CB.COMPANIA
     AND DC.ANO=CB.ANO
     AND DC.CODIGO_CUENTA=CB.IDCONTABLE
  INNER JOIN BANCO B
     ON CB.COMPANIA=B.COMPANIA
     AND  CB.BANCO=B.BANCO
   LEFT JOIN (SELECT DC.COMPANIA,
                     DC.ANO,
                     DC.TIPO_CPTE,
                     DC.COMPROBANTE,
                    SUM(VALOR_CREDITO) DESCUENTO
             FROM DETALLE_COMPROBANTE_CNT DC 
                    INNER JOIN PLAN_CONTABLE P
                     ON DC.COMPANIA=P.COMPANIA
                     AND DC.CUENTA=P.CODIGO
AND DC.ANO=P.ANO
              WHERE DC.COMPANIA=s$compania$s
                   AND DC.ANO=s$ano$s
  AND DC.MES<=s$mesFinal$s
                   AND P.TIPODESCUENTO_SIA IS NOT NULL
             GROUP BY DC.COMPANIA,
                      DC.ANO,
                      DC.TIPO_CPTE,
                      DC.COMPROBANTE) DESCUENTOS       
          ON DC.COMPANIA=DESCUENTOS.COMPANIA
          AND DC.ANO=DESCUENTOS.ANO
          AND DC.TIPO_CPTE=DESCUENTOS.TIPO_CPTE
          AND DC.COMPROBANTE=DESCUENTOS.COMPROBANTE
WHERE DC.COMPANIA=s$compania$s
      AND DC.ANO=s$ano$s
      AND DC.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s 
      AND DC.CLASE_CONTABLE IN ('E','G','A')
      AND CP.COMPANIA  IS NULL
      AND DC.CLASECUENTA IN('B')
