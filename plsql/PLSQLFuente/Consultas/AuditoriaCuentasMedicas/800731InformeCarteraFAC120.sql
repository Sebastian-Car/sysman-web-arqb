MERGE INTO CONSULTAS FIN USING (SELECT '800731InformeCarteraFAC120' INFORME ,TO_CLOB(q'[WITH CAG_AGR AS (
  SELECT
    COMPANIA,
    CONSECUTIVO_ARCHIVO,
    COD_PREST_SERV_SALUD,
    NUM_FACTURA,
    SUM(VALOR_NETO_A_PAGAR_ENTI_CONTR) AS VALOR_NETO_A_PAGAR_ENTI_CONTR,
    SUM(VALOR_OBJECION)                AS VALOR_OBJECION,
    SUM(GLOSA_ACEPTADA_IPS)            AS GLOSA_ACEPTADA_IPS,
    SUM(TOTAL_APROBADO_PAGAR)          AS TOTAL_APROBADO_PAGAR
  FROM CM_AUDITORIA_GLOSAS
  GROUP BY COMPANIA, CONSECUTIVO_ARCHIVO, COD_PREST_SERV_SALUD, NUM_FACTURA
)
SELECT  '2' AS "Tipo registro",
        ROW_NUMBER() OVER (ORDER BY CAT.CONSECUTIVO_RIPS DESC, CAT.NUM_FACTURA) AS "Consecutivo",
        CASE T.TIPOENTIDAD
          WHEN 'Publica' THEN '1'
          WHEN 'Privada' THEN '2'
          WHEN 'Mixta'   THEN '3'
        END AS "TipoIPS",
        CAT.NUM_IDENTIF_PRESTADOR AS "NIT del IPS", 
        T.NOMBRE AS "Nombre IPS",
        'NO' AS "FalloJudicial",
        ''   AS "Prefijo Factura",
        CAT.NUM_FACTURA AS "Factura",
        TO_CHAR(CAT.FECHA_EXP_FACTURA, 'YYYY-MM-DD') AS "Fecha Emision",
        TO_CHAR(CIR.FECHA, 'YYYY-MM-DD')             AS "Fecha Radicacion",
        'I' AS "Indicador de actualizacion",
        NVL(CAG.VALOR_NETO_A_PAGAR_ENTI_CONTR, 0) AS "Valor Total Facturado",
        NVL(CAG.VALOR_OBJECION, 0)                AS "Valor Auditado",
        NVL(CAG.VALOR_OBJECION - CAG.GLOSA_ACEPTADA_IPS, 0) AS "Valor Glosa Por Conciliar",
        NVL(CAG.GLOSA_ACEPTADA_IPS, 0)            AS "Valor Glosa Definitiva",
        NVL(CAG.TOTAL_APROBADO_PAGAR, 0)          AS "Valor Reconocido Aprobado",
        CASE
          WHEN s$agrupado$s = 1 THEN
            ROUND(
              NVL((
                SELECT SUM(DCC.VALOR_DEBITO)
                FROM DETALLE_COMPROBANTE_CNT DCC
                WHERE DCC.COMPANIA = CAT.COMPANIA
                  AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
                  AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
                  AND DCC.FECHA BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
              ),0)
              *
              CASE
                WHEN NVL((
                  SELECT SUM(CAG2.TOTAL_APROBADO_PAGAR)
                  FROM CM_AUDITORIA_GLOSAS CAG2
                  INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
                    ON CAT2.COMPANIA = CAG2.COMPANIA
                   AND CAT2.CONSECUTIVO_RIPS = CAG2.CONSECUTIVO_ARCHIVO
                   AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
                   AND CAT2.NUM_FACTURA = CAG2.NUM_FACTURA
                  WHERE CAG2.COMPANIA = CAT.COMPANIA
                    AND CAT2.CONSECUTIVO_RIPS = CAT.CONSECUTIVO_RIPS
                ),0) > 0
                THEN NVL(CAG.TOTAL_APROBADO_PAGAR,0) /
                     (
                      SELECT SUM(CAG2.TOTAL_APROBADO_PAGAR)
                      FROM CM_AUDITORIA_GLOSAS CAG2
                      INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
                        ON CAT2.COMPANIA = CAG2.COMPANIA
                       AND CAT2.CONSECUTIVO_RIPS ]') || TO_CLOB(q'[= CAG2.CONSECUTIVO_ARCHIVO
                       AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
                       AND CAT2.NUM_FACTURA = CAG2.NUM_FACTURA
                      WHERE CAG2.COMPANIA = CAT.COMPANIA
                        AND CAT2.CONSECUTIVO_RIPS = CAT.CONSECUTIVO_RIPS
                     )
                ELSE 0
              END
            ,0)

          ELSE
            NVL((
              SELECT SUM(DCC.VALOR_DEBITO)
              FROM DETALLE_COMPROBANTE_CNT DCC
              WHERE DCC.COMPANIA = CAT.COMPANIA
                AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
                AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
                AND DCC.FECHA BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
            ),0)
        END AS "Valor Pagado",
        NVL(CAG.TOTAL_APROBADO_PAGAR, 0)
        -
        (
          CASE
            WHEN s$agrupado$s = 1 THEN
              ROUND(
                NVL((
                  SELECT SUM(DCC.VALOR_DEBITO)
                  FROM DETALLE_COMPROBANTE_CNT DCC
                  WHERE DCC.COMPANIA = CAT.COMPANIA
                    AND DCC.TIPO_CPTE_AFECT = CAT.TIPO_COMPROBANTE
                    AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
                    AND DCC.FECHA BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
                ),0)
                *
                CASE
                  WHEN NVL((
                    SELECT SUM(CAG2.TOTAL_APROBADO_PAGAR)
                    FROM CM_AUDITORIA_GLOSAS CAG2
                    INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
                      ON CAT2.COMPANIA = CAG2.COMPANIA
                     AND CAT2.CONSECUTIVO_RIPS = CAG2.CONSECUTIVO_ARCHIVO
                     AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
                     AND CAT2.NUM_FACTURA = CAG2.NUM_FACTURA
                    WHERE CAG2.COMPANIA = CAT.COMPANIA
                      AND CAT2.CONSECUTIVO_RIPS = CAT.CONSECUTIVO_RIPS
                  ),0) > 0
                  THEN NVL(CAG.TOTAL_APROBADO_PAGAR,0) /
                       (
                        SELECT SUM(CAG2.TOTAL_APROBADO_PAGAR)
                        FROM CM_AUDITORIA_GLOSAS CAG2
                        INNER JOIN CM_ARCHIVO_TRANSACCIONES CAT2
                          ON CAT2.COMPANIA = CAG2.COMPANIA
                         AND CAT2.CONSECUTIVO_RIPS = CAG2.CONSECUTIVO_ARCHIVO
                         AND CAT2.COD_PREST_SERV_SALUD = CAG2.COD_PREST_SERV_SALUD
                         AND CAT2.NUM_FACTURA = CAG2.NUM_FACTURA
                        WHERE CAG2.COMPANIA = CAT.COMPANIA
                          AND CAT2.CONSECUTIVO_RIPS = CAT.CONSECUTIVO_RIPS
                       )
                  ELSE 0
                END
              ,0)
            ELSE
              NVL((
                SELECT SUM(DCC.VALOR_DEBITO)
                FROM DETALLE_COMPROBANTE_CNT DCC
                WHERE DCC.COMPANIA = CAT.COMPANIA
                  AND DCC.TIPO_CPTE_]') || TO_CLOB(q'[AFECT = CAT.TIPO_COMPROBANTE
                  AND DCC.CMPTE_AFECTADO  = CAT.NUMERO_COMPROBANTE
                  AND DCC.FECHA BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
              ),0)
          END
        ) AS "Valor pendiente de pago",
        0    AS "Valor Pago anticipado pendiente por Auditar",
        'NO' AS "Factura en Procesojuridico",
        0    AS "Etapa Proceso Juridico",
        ''   AS "Numero_Proceso"
FROM CM_ARCHIVO_TRANSACCIONES CAT
INNER JOIN TERCERO T
  ON T.COMPANIA = CAT.COMPANIA
 AND T.NIT      = CAT.NUM_IDENTIF_PRESTADOR
INNER JOIN CM_IMPORTARRIPS CIR
  ON CIR.COMPANIA    = CAT.COMPANIA
 AND CIR.CONSECUTIVO = CAT.CONSECUTIVO_RIPS
INNER JOIN CAG_AGR CAG                        
  ON CAG.COMPANIA = CAT.COMPANIA
 AND CAG.CONSECUTIVO_ARCHIVO = CAT.CONSECUTIVO_RIPS
 AND CAG.COD_PREST_SERV_SALUD = CAT.COD_PREST_SERV_SALUD
 AND CAG.NUM_FACTURA = CAT.NUM_FACTURA
WHERE CAT.COMPANIA = 's$compania$s'
  AND CAT.CAUSADO NOT IN (0)
  AND CIR.FECHA BETWEEN 's$fechaInicial$s' AND 's$fechaFinal$s'
  AND CIR.CLASECUENTA BETWEEN 's$claseInicial$s' AND 's$claseFinal$s'
ORDER BY CAT.CONSECUTIVO_RIPS DESC, CAT.NUM_FACTURA]') CONSULTA, 84 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);