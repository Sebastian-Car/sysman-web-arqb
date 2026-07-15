WITH MOVIMIENTOS_RECLASIFICACION AS ( SELECT  DETALLE_COMPROBANTE_CNT.COMPANIA,
                                              DETALLE_COMPROBANTE_CNT.ANO,
                                              DETALLE_COMPROBANTE_CNT.CUENTA AS CUENTA,
                                              SUM(CASE
                                                  WHEN DETALLE_COMPROBANTE_CNT.NATURALEZA = 'D'
                                                  THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
                                                  ELSE DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                              END) AS VALOR,
                                              CASE
                                                WHEN TIPO_CPTE='s$parAenValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)
                                                ELSE 0
                                              END AS AENVALOR_DEBITO,
                                              CASE
                                                WHEN TIPO_CPTE='s$parAenValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)
                                                ELSE 0
                                              END AS AENVALOR_CREDITO,
                                              CASE
                                                WHEN TIPO_CPTE='s$parAcnValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)
                                                ELSE 0
                                              END AS ACNVALOR_DEBITO,
                                              CASE
                                                WHEN TIPO_CPTE='s$parAcnValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)
                                                ELSE 0
                                              END AS ACNVALOR_CREDITO,
                                              CASE
                                                WHEN TIPO_CPTE='s$parReValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)
                                                ELSE 0
                                              END AS REVALOR_DEBITO,
                                              CASE
                                                WHEN TIPO_CPTE='s$parReValor$s'
                                                THEN SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)
                                                ELSE 0
                                              END AS REVALOR_CREDITO,
                                              DETALLE_COMPROBANTE_CNT.TIPO_CPTE
                                      FROM DETALLE_COMPROBANTE_CNT
                                      INNER JOIN  TIPO_COMPROBANTE
                                              ON  (DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                             AND   DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO)
                                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = s$compania$s
                                        AND DETALLE_COMPROBANTE_CNT.ANO      = PCK_SYSMAN_UTL.FC_ANIO(SYSDATE)
                                        AND TIPO_COMPROBANTE.CLASE_CONTABLE IN ('Z')
                                        AND TIPO_COMPROBANTE.CODIGO     NOT IN ('CIE')
                                      GROUP BY  DETALLE_COMPROBANTE_CNT.COMPANIA,
                                                DETALLE_COMPROBANTE_CNT.ANO,
                                                DETALLE_COMPROBANTE_CNT.CUENTA,
                                                DETALLE_COMPROBANTE_CNT.TIPO_CPTE)
SELECT  TO_CHAR(LENGTH(PLAN_CONTABLE.CODIGO)) AS NIVEL,
        PLAN_CONTABLE.CODIGO,
        PLAN_CONTABLE.NOMBRE  AS CUENTA,
        PLAN_CONTABLE.SALDOs$mesTrabajo1$s AS "SALDOS PCGA",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.AENVALOR_DEBITO, 0))  AS "AJUSTES ERRORES DEBITO",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.AENVALOR_CREDITO, 0)) AS "AJUSTES ERRORES CREDITO",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.ACNVALOR_DEBITO, 0))  AS "AJUSTES CONVERGENCIA DEBITO",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.ACNVALOR_CREDITO, 0)) AS "AJUSTES CONVERGENCIA CREDITO",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.REVALOR_DEBITO, 0))   AS "RECLASIFICACIONES DEBITO",
        SUM(NVL(MOVIMIENTOS_RECLASIFICACION.REVALOR_CREDITO, 0))  AS "RECLASIFICACIONES CREDITO",
        (PLAN_CONTABLE.SALDOs$mesTrabajo$s + NVL(MOVIMIENTOS_RECLASIFICACION.VALOR, 0)) AS "SALDOS NIIF",
        PLAN_CONTABLE.CODIGO_NIIF AS "ID NIIF"
FROM  PLAN_CONTABLE
      LEFT JOIN MOVIMIENTOS_RECLASIFICACION
        ON (PLAN_CONTABLE.CODIGO    = MOVIMIENTOS_RECLASIFICACION.CUENTA
       AND  PLAN_CONTABLE.ANO       = MOVIMIENTOS_RECLASIFICACION.ANO
       AND  PLAN_CONTABLE.COMPANIA  = MOVIMIENTOS_RECLASIFICACION.COMPANIA)
WHERE PLAN_CONTABLE.COMPANIA = s$compania$s
  AND PLAN_CONTABLE.ANO      = s$anoTrabajo$s
  AND PLAN_CONTABLE.CODIGO BETWEEN 's$codigoInicial$s' AND 's$codigoFinal$s'
  AND LENGTH(PLAN_CONTABLE.CODIGO)    <= s$digitos$s
  AND (ROUND(PLAN_CONTABLE.SALDOs$mesTrabajo$s,2) NOT IN (0
)
       OR ROUND(PLAN_CONTABLE.DEBITOs$mesTrabajo$s,2)  NOT IN (0
)
       OR ROUND(PLAN_CONTABLE.CREDITOs$mesTrabajo$s,2)  NOT IN (0)
       OR ROUND(PLAN_CONTABLE.SALDOs$mesTrabajo1$s,2)   NOT IN (0))
GROUP BY PLAN_CONTABLE.SALDOs$mesTrabajo1$s,
        PLAN_CONTABLE.COMPANIA,
        PLAN_CONTABLE.ANO,
        PLAN_CONTABLE.CODIGO,
        PLAN_CONTABLE.CODIGO_NIIF,
        PLAN_CONTABLE.NOMBRE,
        PLAN_CONTABLE.SALDOs$mesTrabajo$s + NVL(MOVIMIENTOS_RECLASIFICACION.VALOR, 0)
ORDER BY  PLAN_CONTABLE.COMPANIA,
          PLAN_CONTABLE.ANO,
          CASE
            WHEN CODIGO_NIIF IS NULL
              THEN CODIGO
              ELSE CODIGO_NIIF
          END
