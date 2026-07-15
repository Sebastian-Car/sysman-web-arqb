MERGE INTO CONSULTAS FIN USING (SELECT '800582InformeCarteraFT033' INFORME ,TO_CLOB(q'[SELECT
    CASE T.TIPOID
        WHEN 'N'  THEN 'NI'
        WHEN 'C'  THEN 'CC'
        WHEN 'E'  THEN 'CE'
        WHEN 'I'  THEN 'PT'
        WHEN 'DE' THEN 'DE'
        ELSE 'OT'
    END                                                             AS "Tipo ID Acreedor",
    CAT.NUM_IDENTIF_PRESTADOR                                       AS "ID Acreedor",
    NVL(T.DIGITOVERIFICACION, '0')                                  AS "DV Acreedor",
    T.NOMBRE                                                        AS "Nombre Acreedor",
    CASE T.TIPOENTIDAD
        WHEN 'Publica' THEN 'PU'
        WHEN 'Privada' THEN 'PR'
        WHEN 'Mixta'   THEN 'MX'
        ELSE 'PR'
    END                                                             AS "Naturaleza Acreedor",
    'MIGR'                                                          AS "Tipo Poblacion",
    EXTRACT(YEAR FROM CIR.FECHA)                                    AS "Vigencia Deuda",
    NVL(CAG_AGR.VALOR_RADICADO,      0)                             AS "Valor Radicado",
    NVL(CAG_AGR.PENDIENTE_AUDITAR,   0)                             AS "Pendiente Auditar",
    NVL(CAG_AGR.VALOR_AUDITADO,      0)                             AS "Valor Auditado",
    NVL(CAG_AGR.VALOR_GLOSA,         0)                             AS "Valor Glosa",
    NVL(CAG_AGR.PENDIENTE_CONCILIAR, 0)                             AS "Pendiente Conciliar",
    NVL((
        SELECT SUM(DCC.VALOR_DEBITO)
        FROM DETALLE_COMPROBANTE_CNT DCC
        WHERE DCC.COMPANIA        = CAT.COMPANIA
          AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
          AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
          AND DCC.FECHA BETWEEN 's$fechaInicial$s'
                             AND 's$fechaFinal$s'
    ), 0) AS "Pagado Reconocido",
    NVL(CAG_AGR.VALOR_RECONOCIDO, 0) AS "Valor Reconocido",
    NVL((
        SELECT SUM(DCC.VALOR_DEBITO)
        FROM DETALLE_COMPROBANTE_CNT DCC
        WHERE DCC.COMPANIA        = CAT.COMPANIA
          AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
          AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
          AND DCC.FECHA BETWEEN 's$fechaInicial$s'
                             AND 's$fechaFinal$s'
    ), 0) AS "Valor Pagado",
    NVL(CAG_AGR.VALOR_RECONOCIDO, 0)
    -
    NVL((
        SELECT SUM(DCC.VALOR_DEBITO)
        FROM DETALLE_COMPROBANTE_CNT DCC
        WHERE DCC.COMPANIA        = CAT.COMPANIA
          AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
          AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
          AND DCC.FECHA BETWEEN 's$fechaInicial$s'
                             AND 's$fechaFinal$s'
    ), 0) AS "Pendiente Pago",
    0    AS "Anticipo Pendiente",
    0    AS "SGP Prestacion",
    0    AS "Excedente SGP",
    0    AS "Impuesto Licor",
    0    AS "Impuesto Cerveza",
    0    AS "Excedente Rentas",
    0    AS "Excedente Maestra",
    0    AS "Transferencias MSPS",
    0    AS "Otros Recursos",
    'NA' AS "Cuales Otros"
FROM CM_ARCHIVO_TRANSACCIONES  ]') || TO_CLOB(q'[ CAT
INNER JOIN TERCERO              T
       ON  T.COMPANIA           = CAT.COMPANIA
       AND T.NIT                = CAT.NUM_IDENTIF_PRESTADOR
INNER JOIN CM_IMPORTARRIPS      CIR
       ON  CIR.COMPANIA         = CAT.COMPANIA
       AND CIR.CONSECUTIVO      = CAT.CONSECUTIVO_RIPS
INNER JOIN (
    SELECT
        CAT_I.COMPANIA,
        CAT_I.CONSECUTIVO_RIPS,
        CAT_I.TIPO_COMPROBANTE,
        CAT_I.NUMERO_COMPROBANTE,
        NVL(SUM(CAG_I.VALOR_RADICADO), 0)          AS VALOR_RADICADO,
        NVL(SUM(CAG_I.PENDIENTE_AUDITAR), 0)       AS PENDIENTE_AUDITAR,
        NVL(SUM(CAG_I.VALOR_AUDITADO), 0)          AS VALOR_AUDITADO,
        NVL(SUM(CAG_I.VALOR_GLOSA), 0)             AS VALOR_GLOSA,
        NVL(SUM(CAG_I.PENDIENTE_CONCILIAR), 0)     AS PENDIENTE_CONCILIAR,
        NVL(SUM(CAG_I.VALOR_RECONOCIDO), 0)        AS VALOR_RECONOCIDO
    FROM CM_ARCHIVO_TRANSACCIONES  CAT_I
    INNER JOIN (
        SELECT
            COMPANIA,
            CONSECUTIVO_ARCHIVO,
            COD_PREST_SERV_SALUD,
            NUM_FACTURA,
            SUM(VALOR_NETO_A_PAGAR_ENTI_CONTR) AS VALOR_RADICADO,
            SUM(
                CASE
                    WHEN NVL(TOTAL_APROBADO_PAGAR,0) = 0
                    THEN NVL(VALOR_OBJECION,0)
                         - NVL(GLOSA_ACEPTADA_IPS,0)
                    ELSE 0
                END
            ) AS PENDIENTE_AUDITAR,

            SUM(
                CASE
                    WHEN NVL(TOTAL_APROBADO_PAGAR,0) > 0
                    THEN NVL(VALOR_OBJECION,0)
                    ELSE 0
                END
            ) AS VALOR_AUDITADO,
            SUM(GLOSA_ACEPTADA_IPS) AS VALOR_GLOSA,
            SUM(
                VALOR_OBJECION
                - GLOSA_ACEPTADA_IPS
            ) AS PENDIENTE_CONCILIAR,
            SUM(TOTAL_APROBADO_PAGAR) AS VALOR_RECONOCIDO
        FROM CM_AUDITORIA_GLOSAS
        GROUP BY
            COMPANIA,
            CONSECUTIVO_ARCHIVO,
            COD_PREST_SERV_SALUD,
            NUM_FACTURA
    ) CAG_I
           ON  CAG_I.COMPANIA             = CAT_I.COMPANIA
           AND CAG_I.CONSECUTIVO_ARCHIVO  = CAT_I.CONSECUTIVO_RIPS
           AND CAG_I.COD_PREST_SERV_SALUD = CAT_I.COD_PREST_SERV_SALUD
           AND CAG_I.NUM_FACTURA          = CAT_I.NUM_FACTURA
    WHERE CAT_I.COMPANIA = 's$compania$s'
    GROUP BY
        CAT_I.COMPANIA,
        CAT_I.CONSECUTIVO_RIPS,
        CAT_I.TIPO_COMPROBANTE,
        CAT_I.NUMERO_COMPROBANTE
) CAG_AGR
       ON  CAG_AGR.COMPANIA          = CAT.COMPANIA
	AND CAG_AGR.CONSECUTIVO_RIPS  = CAT.CONSECUTIVO_RIPS
	AND CAG_AGR.TIPO_COMPROBANTE  = CAT.TIPO_COMPROBANTE
	AND CAG_AGR.NUMERO_COMPROBANTE = CAT.NUMERO_COMPROBANTE
WHERE CAT.COMPANIA   = 's$compania$s'
  AND CAT.CAUSADO    NOT IN (0)
  AND CIR.FECHA      BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
  AND CIR.CLASECUENTA BETWEEN 's$claseInicial$s' AND 's$claseFinal$s'
GROUP BY
    T.TIPOID,
    CAT.NUM_IDENTIF_PRESTADOR,
    T.DIGITOVERIFICACION,
    T.NOMBRE,
    T.TIPOENTIDAD]') || TO_CLOB(q'[,
    EXTRACT(YEAR FROM CIR.FECHA),
    CAG_AGR.VALOR_RADICADO,
    CAG_AGR.PENDIENTE_AUDITAR,
    CAG_AGR.VALOR_AUDITADO,
    CAG_AGR.VALOR_GLOSA,
    CAG_AGR.PENDIENTE_CONCILIAR,
    CAG_AGR.VALOR_RECONOCIDO,
    CAT.COMPANIA,
    CAT.TIPO_COMPROBANTE,
    CAT.NUMERO_COMPROBANTE,
    CAT.CONSECUTIVO_RIPS
ORDER BY
    CAT.NUM_IDENTIF_PRESTADOR,
    EXTRACT(YEAR FROM CIR.FECHA)]') CONSULTA, 84 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);