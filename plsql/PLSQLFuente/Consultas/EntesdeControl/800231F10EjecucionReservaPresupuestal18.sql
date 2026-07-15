SELECT CODIGO_SIA ||RESERVA.ID                    "Código Rubro Presupuestal",
       VP.NOMBRE                                  "Nombre Rubro Presupuestal",
       RESERVA_CONSTITUIDA                        "Reserva Constituida",          
       ACTA                                       "Acta De Cancelación",
       PAGO_RESERVA                               "Pago" 
      
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
                   MODIFICACION_RESERVA.FECHA_ACTA,
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
                                   TO_CHAR(CP.FECHA_VCN_DOC) FECHA_ACTA,                                                 
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
                                WHERE D.COMPANIA=s$compania$s
                                AND D.ANO=s$ano$s
                                AND T.CLASE IN ('ADR','DMR','TRA')
                                AND P.TIPOVIGENCIA IN ('RA')
                                AND D.MES <= s$mesFinal$s
                                GROUP BY D.COMPANIA,
                                         D.ANO,
                                         D.ID,
                                         CP.NRO_DOCUMENTO,
                                         TO_CHAR(CP.FECHA_VCN_DOC)) MODIFICACION_RESERVA 
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
                                WHERE D.COMPANIA=s$compania$s
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
                     MODIFICACION_RESERVA.FECHA_ACTA,
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
       AND RESERVA.ANO=s$ano$s
