MERGE INTO CONSULTAS FIN USING (SELECT '800567V_CGR_Relacion_IngresosAppUI_SINFUE' INFORME ,TO_CLOB(q'[SELECT codigoPresupuestal,
vigencia,
macroCampo,
fechaRecaudo,
numeroRecibo,
numeroReconocimiento,
cuentaBancariaOrig,
cuentaContable,
nit,
nombre,
conceptoRecaudo,
valor,cuentaBancariaDest,
nombreBanco

FROM(
SELECT  
     NVL(CODIGO_CCPET.CODIGO,D.CUENTA) codigoPresupuestal,    
     DECODE (PLAN_PRESUPUESTAL.TIPOVIGENCIA,'RA','2','RC','3','1') vigencia,
    '2' macroCampo,
    TO_CHAR(D.FECHA, 'YYYY-MM-DD') fechaRecaudo,
    TO_CHAR(D.COMPROBANTE) numeroRecibo,
    NVL(COMPROBANTES_CNT.NOMBREBANCO, 'NA') nombreBanco,
    '0' cuentaBancariaOrig,
    NVL(REPLACE(AFECT.CUENTA, '-', ''), 0) cuentaContable,    
    NVL(TO_CHAR(AFECT.CMPTE_AFECTADO), 0) numeroReconocimiento,
    D.TERCERO   nit,
    NVL(TER.NOMBRE, 'N/A') nombre,
    REPLACE(REPLACE(REPLACE(REPLACE(NVL(D.DESCRIPCION, 'N/A'), CHR(13), ' '), CHR(10), ' '),'#',''),'-','') conceptoRecaudo,
    NVL(D.VALOR_CREDITO,0)-NVL(D.VALOR_DEBITO,0) valor ,
    NVL(REPLACE(COMPROBANTES_CNT.NUMEROCUENTA, '-', ''), 0) cuentaBancariaDest

FROM
    COMPANIA                    C
    INNER JOIN TIPO_COMPROBPP              T ON C.CODIGO = T.COMPANIA
    INNER JOIN 
        (SELECT COMPANIA, ANO, TIPO_CPTE,FECHA, MES, COMPROBANTE,CUENTA CODIGO, CODIGO_CCPET CUENTA, TERCERO, SUCURSAL,
                TERCEROI, SUCURSALI, AUXILIARI, CENTRO_COSTOI, FUENTE_RECURSOI, REFERENCIAI,
                MIN(DESCRIPCION) DESCRIPCION,
                SUM(VALOR_CREDITO) VALOR_CREDITO,
                SUM(VALOR_DEBITO) VALOR_DEBITO
         FROM DETALLE_COMPROBANTE_PPTAL 
         WHERE COMPANIA = s$compania$s
           AND ANO=s$ano$s 
         GROUP BY COMPANIA, ANO, TIPO_CPTE,FECHA, MES, COMPROBANTE,CUENTA, CODIGO_CCPET, TERCERO, SUCURSAL,
                TERCEROI, SUCURSALI, AUXILIARI, CENTRO_COSTOI, FUENTE_RECURSOI, REFERENCIAI
          ORDER BY TO_CHAR(DETALLE_COMPROBANTE_PPTAL.FECHA,'DD/MM/YYYY')
         )  D ON D.COMPANIA = T.COMPANIA
         AND D.TIPO_CPTE = T.CODIGO
    INNER JOIN PLAN_PRESUPUESTAL ON D.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                    AND D.ANO = PLAN_PRESUPUESTAL.ANO
                                    AND D.CODIGO = PLAN_PRESUPUESTAL.CODIGO
    INNER JOIN TERCERO             TER ON D.COMPANIA = TER.COMPANIA
                                          AND D.SUCURSAL = TER.SUCURSAL
                                          AND D.TERCERO = TER.NIT
    LEFT JOIN (
        SELECT UNICO.COMPANIA,
                                  UNICO.ANO,
                                  UNICO.TIPO_CPTE,
                                  UNICO.COMPROBANTE,
                                  C.CUENTANUMERO NUMEROCUENTA,
                                  B.NOMBREBANCO 
                    FROM (
                    SELECT CN.COMPANIA,
                                  CN.ANO,
                                  CN.TIPO_CPTE,
                                  CN.COMPROBANTE,
                                   MIN(CUENTA) CUENTA

                      FROM DETALLE_COMPROBANTE_CNT CN       INN]') || TO_CLOB(q'[ER JOIN PLAN_CONTABLE P
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
                AND D.TIPO_CPTE=COMPROBANTES_CNT.TIPO_CPTE
                AND D.COMPROBANTE=COMPROBANTES_CNT.COMPROBANTE   

    LEFT JOIN (
        SELECT
            DETALLE_COMPROBANTE_CNT.COMPANIA,
            DETALLE_COMPROBANTE_CNT.ANO,
            DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
            DETALLE_COMPROBANTE_CNT.COMPROBANTE,
            DETALLE_COMPROBANTE_CNT.CUENTA,

            DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
            DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
            DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO,
            SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) VALOR_CREDITO,
            SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR_DEBITO

        FROM
            TIPO_COMPROBANTE
            INNER JOIN DETALLE_COMPROBANTE_CNT ON DETALLE_COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA

                                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
        WHERE
            TIPO_COMPROBANTE.COMPANIA = s$compania$s
            AND TIPO_COMPROBANTE.CLASE_CONTABLE IN (
                'I',
                'B',
                'S',
                'J',
                'R'
            )
            AND DETALLE_COMPROBANTE_CNT.ANO = s$ano$s 
            AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
        GROUP BY DETALLE_COMPROBANTE_CNT.COMPANIA,
            DETALLE_COMPROBANTE_CNT.ANO,
            DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
            DETALLE_COMPROBANTE_CNT.COMPROBANTE,
            DETALLE_COMPROBANTE_CNT.CUENTA,
            DETALLE_COMPROBANTE]') || TO_CLOB(q'[_CNT.CUENTAPPTAL,
            DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
            DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO    
    ) AFECT ON D.COMPANIA = AFECT.COMPANIA
               AND D.ANO = AFECT.ANO
               AND D.TIPO_CPTE      = AFECT.TIPO_CPTE
               AND D.COMPROBANTE    = AFECT.COMPROBANTE
               AND D.CUENTA         = AFECT.CUENTAPPTAL
               AND D.VALOR_CREDITO  = AFECT.VALOR_CREDITO
               AND D.VALOR_DEBITO   = AFECT.VALOR_DEBITO

    LEFT JOIN TIPOCLASIFICADOR CODIGO_CCPET
    ON D.COMPANIA = CODIGO_CCPET.COMPANIA
    AND D.ANO = CODIGO_CCPET.ANO
    AND '006'= CODIGO_CCPET.CLASECLASIFICADOR
    AND D.CUENTA = CODIGO_CCPET.CODIGO



WHERE
    T.COMPANIA = s$compania$s
    AND T.CLASE IN (
        'ING',
        'DIN',
        'AIN'
    )
    AND PLAN_PRESUPUESTAL.ANO = s$ano$s 
    AND D.MES BETWEEN s$mesInicial$s AND s$mesFinal$s

    AND PLAN_PRESUPUESTAL.REGALIAS IN (0)
    AND NVL(D.VALOR_CREDITO,0)-NVL(D.VALOR_DEBITO,0)<>'0'
ORDER BY
    TO_CHAR(D.FECHA, 'YYYY-MM-DD'))]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);