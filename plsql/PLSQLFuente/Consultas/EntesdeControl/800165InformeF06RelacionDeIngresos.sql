MERGE INTO CONSULTAS FIN USING (SELECT '800165InformeF06RelacionDeIngresos' INFORME ,TO_CLOB(q'[SELECT CODIGO_SIA||D.CUENTA                       CODIGO_PRESUPUESTAL,
       TO_CHAR(D.FECHA,'DD/MM/YYYY')              FECHA_RECIBO ,
	   PCK_SYSMAN_UTL.FC_NOMBRE_MES(s$mesFinal$s) MESREPORTADO
,
       D.TIPO|| '-'||D.COMPROBANTE                NUMERO_DE_RECIBO,
       D.TERCERO_DET_NOM                          RECIBIDO_DE,
       D.DESCRIPCION_DET||TEXTO                   CONCEPTO_RECAUDO,
       (D.VALOR_CREDITO-D.VALOR_DEBITO)           VALOR,
      COMPROBANTES_CNT.NUMEROCUENTA               CUENTA_BANCARIA_DESTINO,
      COMPROBANTES_CNT.CODBANCO_SIA               BANCO
FROM V_DETALLE_AUXILIAR_PPTAL D INNER JOIN TIPO_COMPROBPP T
           ON  D.COMPANIA=T.COMPANIA
           AND D.TIPO=T.CODIGO
      LEFT JOIN (
        SELECT UNICO.COMPANIA,
                                  UNICO.ANO,
                                  UNICO.TIPO_CPTE,
                                  UNICO.COMPROBANTE,
                                  B.NOMBREBANCO||'-'||C.CUENTANUMERO NUMEROCUENTA,
                                 C.CODBANCO_SIA
                    FROM (
                    SELECT CN.COMPANIA,
                                  CN.ANO,
                                  CN.TIPO_CPTE,
                                  CN.COMPROBANTE,
                                   MIN(CUENTA) CUENTA
                      FROM DETALLE_COMPROBANTE_CNT CN       INNER JOIN PLAN_CONTABLE P
						   ON  P.COMPANIA=CN.COMPANIA
                             AND P.ANO=CN.ANO
                             AND P.CODIGO=CN.CUENTA
                             INNER JOIN TIPO_COMPROBANTE T
                             ON  CN.COMPANIA=T.COMPANIA
                              AND CN.TIPO_CPTE=T.CODIGO
                     WHERE    CN.COMPANIA=s$compania$s
                             AND CN.ANO=s$ano$s
                             AND P.CLASECUENTA IN ('B')
                             AND CN.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
                             AND T.CLASE_CONTABLE IN('I','B','S','J')
                       GROUP BY
                                  CN.COMPANIA,
                                  CN.ANO,
                                  CN.TIPO_CPTE,
                                  CN.COMPROBANTE) UNICO
                      INNER JOIN CUENTABANCOS C
                            ON UNICO.COMPANIA=C.COMPANIA
                            AND UNICO.ANO=C.ANO
                            AND UNICO.CUENTA=C.IDCONTABLE
                      INNER JOIN BANCO B
                             ON C.COMPANIA=B.COMPANIA
                            AND C.BANCO=B.BANCO )
    COMPROBANTES_CNT
          ON    D.COMPANIA=COMPROBANTES_CNT.COMPANIA
                AND D.ANO=COMPROBANTES_CNT.ANO
                AND D.TIPO=COMPROBANTES_CNT.TIPO_CPTE
                AND D.COMPROBANTE=COMPROBANTES_CNT.COMPROBANTE
  LEFT JOIN  PLAN_PPTAL_CONFIG PC
          ON  D.COMPANIA=PC.COMPANIA
                AND D.ANO=PC.ANO
                AND D.CUENTA=PC.CODIGO
                AND D.CENTRO_COSTO]') || TO_CLOB(q'[=PC.CENTRO_COSTO
                AND D.AUXILIAR=PC.AUXILIAR
                AND D.FUENTE_RECURSOS=PC.FUENTE_RECURSO
                AND D.REFERENCIA=PC.REFERENCIA
LEFT  JOIN  PLAN_PRESUPUESTAL
           ON  D.COMPANIA  =PLAN_PRESUPUESTAL.COMPANIA
           AND D.ANO           =PLAN_PRESUPUESTAL.ANO
           AND D.CUENTA    =PLAN_PRESUPUESTAL.CODIGO
  WHERE D.COMPANIA= s$compania$s
   AND D.ANO= s$ano$s
   AND D.MES BETWEEN   s$mesInicial$s AND  s$mesFinal$s
   AND T.CLASE IN ('ING','DIN','AIN')
   AND PLAN_PRESUPUESTAL.REGALIAS IN (0)]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);