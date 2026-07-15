MERGE INTO CONSULTAS FIN USING (SELECT '800559InformeF10ReservasTelePacifico' INFORME ,TO_CLOB(q'[SELECT CODIGO_SIA||RESERVA.ID                    CODIGO_RUBRO_PRESUPUESTAL,
       VP.NOMBRE                                  NOMBRE_RUBRO_PRESUPUESTAL,
       RESERVA_CONSTITUIDA                        VALOR_RESERVA_CONSTITUIDA,          
       NVL(ACTA,'ND')                                       No_ACTA_DE_LA_CANCELACION,
       PAGO_RESERVA                               VALOR_PAGADO        
FROM PLAN_PPTAL_CONFIG LEFT JOIN 
            (SELECT D.COMPANIA,
                   D.ANO,
                   D.ID,
                   D.CUENTA,
                   D.FUENTE_RECURSO,
                   D.CENTRO_COSTO,
                   D.AUXILIAR,
                   D.REFERENCIA,
                   SUM(VALOR_DEBITO) RESERVA_CONSTITUIDA,
                   MODIFICACION_RESERVA.ACTA,
                   'ND'                  FECHA_ACTA,
                   NVL(MODIFICACION_RESERVA.MODIFICACION,0) MODIFICACION_RESERVA,
                   NVL(PAGO_RESERVA.PAGOS_RESERVA,0) PAGO_RESERVA,
                   P.VIGENCIA VIGENCIA_RESERVA
                FROM DETALLE_COMPROBANTE_PPTAL D  INNER JOIN TIPO_COMPROBPP T
                       ON D.COMPANIA=T.COMPANIA
                       AND D.TIPO_CPTE=T.CODIGO
                 INNER JOIN V_PLAN_PRESUPUESTAL P
                       ON D.COMPANIA=P.COMPANIA
                         AND D.ANO=P.ANO
                         AND D.ID=P.ID         
                 LEFT JOIN (SELECT D.COMPANIA,
                                   D.ANO,
                                   D.ID,
                                   CP.NRO_DOCUMENTO ACTA,                                                   
                                   ABS(SUM(VALOR_DEBITO-VALOR_CREDITO)) 
                                         MODIFICACION                     
                              FROM DETALLE_COMPROBANTE_PPTAL D  INNER JOIN TIPO_COMPROBPP T
                                           ON D.COMPANIA=T.COMPANIA
                                           AND D.TIPO_CPTE=T.CODIGO
                                     INNER JOIN V_PLAN_PRESUPUESTAL P
                                           ON D.COMPANIA=P.COMPANIA
                                             AND D.ANO=P.ANO
                                             AND D.ID=P.ID
                                     INNER JOIN COMPROBANTE_PPTAL CP
                                           ON D.COMPANIA=CP.COMPANIA
                                             AND D.ANO=CP.ANO
                                             AND D.TIPO_CPTE=CP.TIPO
                                             AND D.COMPROBANTE=CP.NUMERO
                                WHERE D.COMPANIA= s$compania$s
                                AND D.ANO=s$ano$s
                                AND T.CLASE IN ('ADR','DMR','TRA')
                                AND P.TIPOVIGENCIA IN ('RA')
                                AND D.MES <= s$mesFinal$s
                                GROUP BY D]') || TO_CLOB(q'[.COMPANIA,
                                         D.ANO,
                                         D.ID,
                                         CP.NRO_DOCUMENTO
                                         ) MODIFICACION_RESERVA 
                        ON D.COMPANIA=MODIFICACION_RESERVA.COMPANIA
                         AND D.ANO=MODIFICACION_RESERVA.ANO
                         AND D.ID=MODIFICACION_RESERVA.ID                     
                LEFT JOIN (SELECT D.COMPANIA,
                                   D.ANO,
                                   D.ID,
                                   SUM(VALOR_DEBITO) PAGOS_RESERVA
                                FROM DETALLE_COMPROBANTE_PPTAL D  INNER JOIN TIPO_COMPROBPP T
                                           ON D.COMPANIA=T.COMPANIA
                                           AND D.TIPO_CPTE=T.CODIGO
                                     INNER JOIN V_PLAN_PRESUPUESTAL P
                                           ON D.COMPANIA=P.COMPANIA
                                             AND D.ANO=P.ANO
                                             AND D.ID=P.ID
                                WHERE D.COMPANIA= s$compania$s
                                AND D.ANO=s$ano$s
                                AND T.CLASE IN ('EGR')
                                AND P.TIPOVIGENCIA IN ('RA')
                                AND D.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
                                GROUP BY D.COMPANIA,
                                         D.ANO,
                                         D.ID) PAGO_RESERVA
                    ON D.COMPANIA=PAGO_RESERVA.COMPANIA
                         AND D.ANO=PAGO_RESERVA.ANO
                         AND D.ID=PAGO_RESERVA.ID    
            WHERE D.COMPANIA=s$compania$s
            AND D.ANO=s$ano$s
            AND T.CLASE IN ('RES')
            AND P.TIPOVIGENCIA IN ('RA')
            AND D.MES<= s$mesFinal$s
            GROUP BY D.COMPANIA,
                     D.ANO,
                     D.ID,
                     D.CUENTA,
                     MODIFICACION_RESERVA.ACTA,
                     MODIFICACION_RESERVA.MODIFICACION,
                     PAGO_RESERVA.PAGOS_RESERVA,
                     D.FUENTE_RECURSO,
                     D.CENTRO_COSTO,
                     D.AUXILIAR,
                     D.REFERENCIA,
                     P.VIGENCIA) RESERVA
        ON  RESERVA.COMPANIA=PLAN_PPTAL_CONFIG.COMPANIA
        AND RESERVA.ANO=PLAN_PPTAL_CONFIG.ANO
        AND RESERVA.CUENTA=PLAN_PPTAL_CONFIG.CODIGO
        AND RESERVA.CENTRO_COSTO=PLAN_PPTAL_CONFIG.CENTRO_COSTO
        AND RESERVA.AUXILIAR=PLAN_PPTAL_CONFIG.AUXILIAR
        AND RESERVA.FUENTE_RECURSO=PLAN_PPTAL_CONFIG.FUENTE_RECURSO
INNER JOIN V_PLAN_PRESUPUESTAL VP
         ON  RESERVA.COMPANIA=VP.COMPANIA
        AND RESERVA.ANO=VP.ANO
        AND RESERVA.ID=VP.ID
WHERE  RESERVA.COMPANIA=s$compania$s
       AND RESERVA.ANO=s$ano$s]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);