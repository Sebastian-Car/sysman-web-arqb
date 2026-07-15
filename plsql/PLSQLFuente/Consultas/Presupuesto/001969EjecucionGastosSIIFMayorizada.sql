SELECT
    V_PLAN_PRESUPUESTAL.CODIGO,
    V_PLAN_PRESUPUESTAL.NOMBRE,
    SUM(APROPIADO) APROPIADO,
    SUM(TRASLADO_DEBITO) TRASLADO_DEBITO,
    SUM(TRASLADO_CREDITO) TRASLADO_CREDITO,
    SUM(APRDEFINITIVA) APRDEFINITIVA,
    SUM(VLR_SOLICITUD) VLR_SOLICITUD,
    SUM(VLR_DISPONIBILIDAD) VLR_DISPONIBILIDAD,
    SUM(INCREMENTO_CDP) INCREMENTO_CDP,
    SUM(DECREMENTO_CDP) DECREMENTO_CDP,
    SUM(VALOR_FINAL_DISPONIBILIDAD) VALOR_FINAL_DISPONIBILIDAD,
    SUM(APRDEFINITIVA) - SUM(VALOR_FINAL_DISPONIBILIDAD) SALDO_DE_APROPIACION,
    SUM(VLR_INICIAL_REGISTRO) VLR_INICIAL_REGISTRO,
    SUM(INCREMENTO_CRP) INCREMENTO_CRP,
    SUM(DECREMENTO_CRP) DECREMENTO_CRP,
    SUM(VALOR_FINAL_REGISTRO) VALOR_FINAL_REGISTRO,
    SUM(SALDO_DEL_CDP) SALDO_DEL_CDP,
    SUM(VALOR_FINAL_OBLIGACIONES) VALOR_FINAL_OBLIGACIONES,
    SUM(SALDO_DEL_CRP) SALDO_DEL_CRP,
    SUM(PAGOS) PAGOS,
    ROUND(NVL(SUM(PAGOS),0) /
        CASE
            WHEN SUM(APRDEFINITIVA) = 0   THEN 1
            ELSE SUM(APRDEFINITIVA)
        END
    * 100,2) PORCENTAJE
FROM
    V_PLAN_PRESUPUESTAL
    INNER JOIN (
        WITH TMP_PRESUPUESTOANE AS (
            SELECT
                COMPANIA,
                ANO,
                CODIGO,
                NOMBRE,
                APROPIADO,
                TRASLADO_DEBITO,
                TRASLADO_CREDITO,
                APRDEFINITIVA,
                VLR_SOLICITUD,
                VLR_DISPONIBILIDAD,
                INCREMENTO_CDP,
                DECREMENTO_CDP,
                VALOR_FINAL_DISPONIBILIDAD,
                SALDO_DE_APROPIACION,
                VLR_INICIAL_REGISTRO,
                INCREMENTO_CRP,
                DECREMENTO_CRP,
                VALOR_FINAL_REGISTRO,
                SALDO_DEL_CDP,
                VALOR_FINAL_OBLIGACIONES,
                SALDO_DEL_CRP,
                PAGOS,
                PORCENTAJE
            FROM
                (     SELECT
                        SYSMANEJECUCION.COMPANIA,
                        SYSMANEJECUCION.ANO,
                        SYSMANEJECUCION.CODIGO,
                        SYSMANEJECUCION.NOMBRE,
                        SYSMANEJECUCION.APROPIADO,
                        SYSMANEJECUCION.TRASLADO_DEBITO,
                        SYSMANEJECUCION.TRASLADO_CREDITO,
                        SYSMANEJECUCION.APRDEFINITIVA,
                        SYSMANEJECUCION.VLR_SOLICITUD,
                        SYSMANEJECUCION.VLR_DISPONIBILIDAD,
                        SYSMANEJECUCION.INCREMENTO_CDP,
                        SYSMANEJECUCION.DECREMENTO_CDP,
                        SYSMANEJECUCION.VALOR_FINAL_DISPONIBILIDAD,
                        SYSMANEJECUCION.SALDO_DE_APROPIACION,
                        SYSMANEJECUCION.VLR_INICIAL_REGISTRO,
                        SYSMANEJECUCION.INCREMENTO_CRP,
                        SYSMANEJECUCION.DECREMENTO_CRP,
                        SYSMANEJECUCION.VALOR_FINAL_REGISTRO,
                        SYSMANEJECUCION.SALDO_DEL_CDP,
                        NVL(OBLIGACIONES.VALOR_FINAL_OBLIGACIONES,0) VALOR_FINAL_OBLIGACIONES,
                        NVL(SYSMANEJECUCION.VALOR_FINAL_REGISTRO - OBLIGACIONES.VALOR_FINAL_OBLIGACIONES,0) SALDO_DEL_CRP,
                        NVL(ORDENDEPAGO.PAGOS,0) PAGOS,
                        ROUND(NVL(ORDENDEPAGO.PAGOS,0) /
                            CASE
                                WHEN SYSMANEJECUCION.APRDEFINITIVA = 0   THEN 1
                                ELSE SYSMANEJECUCION.APRDEFINITIVA
                            END
                        * 100,2) PORCENTAJE
                    FROM
                        ( SELECT
                                PLAN_PRESUPUESTAL.COMPANIA,
                                PLAN_PRESUPUESTAL.ANO,
                                PLAN_PRESUPUESTAL.CODIGO,
                                PLAN_PRESUPUESTAL.NOMBRE,
                                PLAN_PRESUPUESTAL.APROPIACIONINICIAL AS APROPIADO,
                                SALDO_AUX_PPTAL.TRASLADO_DEBITO,
                                SALDO_AUX_PPTAL.TRASLADO_CREDITO,
                                PLAN_PRESUPUESTAL.APROPIACIONINICIAL + SALDO_AUX_PPTAL.ADICION + SALDO_AUX_PPTAL.REDUCCION + SALDO_AUX_PPTAL.TRASLADO_DEBITO - SALDO_AUX_PPTAL.TRASLADO_CREDITO AS APRDEFINITIVA,
                                NVL(SIIF_DISPONIBILIDADES.VALOR_INICIAL,0) VLR_SOLICITUD,
                                NVL(SIIF_DISPONIBILIDADES.VALOR_INICIAL,0) VLR_DISPONIBILIDAD,
                                SUM(
                                    CASE
                                        WHEN SIIF_DISPONIBILIDADES.VALOR_OPERACIONES > 0  THEN SIIF_DISPONIBILIDADES.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                                ) AS INCREMENTO_CDP,
                                SUM(
                                    CASE
                                        WHEN SIIF_DISPONIBILIDADES.VALOR_OPERACIONES < 0  THEN SIIF_DISPONIBILIDADES.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                                ) AS DECREMENTO_CDP,
                                NVL(SIIF_DISPONIBILIDADES.VALOR_ACTUAL,0) AS VALOR_FINAL_DISPONIBILIDAD,
                                NVL(PLAN_PRESUPUESTAL.APROPIACIONINICIAL - SIIF_DISPONIBILIDADES.VALOR_ACTUAL,0) AS SALDO_DE_APROPIACION,
                                SUM(NVL(SIIF_REGISTRO_COMPROMISO.VALOR_INICIAL,0) ) VLR_INICIAL_REGISTRO,
                                SUM(
                                    CASE
                                        WHEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES > 0  THEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                                ) AS INCREMENTO_CRP,
                                SUM(
                                    CASE
                                        WHEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES < 0  THEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                                ) AS DECREMENTO_CRP,
                                CASE
                                        WHEN SIIF_REGISTRO_COMPROMISO.VALOR_INICIAL + SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES = 0   THEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                                AS ANULACION_REGISTRO,
                                SUM(NVL(SIIF_REGISTRO_COMPROMISO.VALOR_ACTUAL,0) ) AS VALOR_FINAL_REGISTRO,
                                SUM(NVL(SIIF_DISPONIBILIDADES.VALOR_ACTUAL - SIIF_REGISTRO_COMPROMISO.VALOR_ACTUAL,0) ) SALDO_DEL_CDP
                            FROM PLAN_PRESUPUESTAL 
                            LEFT JOIN SIIF_DISPONIBILIDADES 
                                ON PLAN_PRESUPUESTAL.COMPANIA = SIIF_DISPONIBILIDADES.COMPANIA
                                AND PLAN_PRESUPUESTAL.ANO = TO_NUMBER(TO_CHAR(SIIF_DISPONIBILIDADES.FECHA_DE_REGISTRO,'YYYY') )
                                AND REPLACE(PLAN_PRESUPUESTAL.CODIGO,' ','') = SIIF_DISPONIBILIDADES.RUBRO
                            LEFT JOIN SIIF_REGISTRO_COMPROMISO 
                                ON SIIF_DISPONIBILIDADES.RUBRO = SIIF_REGISTRO_COMPROMISO.RUBRO
                                AND SIIF_DISPONIBILIDADES.NUMERO_DOCUMENTO = SIIF_REGISTRO_COMPROMISO.CDP
                            LEFT JOIN SALDO_AUX_PPTAL 
                                ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                                AND PLAN_PRESUPUESTAL.ANO = SALDO_AUX_PPTAL.ANO
                                AND REPLACE(PLAN_PRESUPUESTAL.CODIGO,' ','') = REPLACE(SALDO_AUX_PPTAL.CODIGO,' ','')
                            WHERE
                                PLAN_PRESUPUESTAL.COMPANIA = s$compania$s
                                AND   PLAN_PRESUPUESTAL.ANO = s$anio$s
                                AND   SALDO_AUX_PPTAL.MES <= s$mes$s
                            GROUP BY
                                PLAN_PRESUPUESTAL.COMPANIA,
                                PLAN_PRESUPUESTAL.ANO,
                                PLAN_PRESUPUESTAL.CODIGO,
                                PLAN_PRESUPUESTAL.NOMBRE,
                                PLAN_PRESUPUESTAL.APROPIACIONINICIAL,
                                SALDO_AUX_PPTAL.TRASLADO_DEBITO,
                                SALDO_AUX_PPTAL.TRASLADO_CREDITO,
                                PLAN_PRESUPUESTAL.APROPIACIONINICIAL + SALDO_AUX_PPTAL.ADICION + SALDO_AUX_PPTAL.REDUCCION + SALDO_AUX_PPTAL.TRASLADO_DEBITO - SALDO_AUX_PPTAL.TRASLADO_CREDITO,
                                SIIF_DISPONIBILIDADES.VALOR_INICIAL,
                                SIIF_DISPONIBILIDADES.VALOR_ACTUAL,
                                PLAN_PRESUPUESTAL.APROPIACIONINICIAL - SIIF_DISPONIBILIDADES.VALOR_ACTUAL,
                                CASE
                                        WHEN SIIF_REGISTRO_COMPROMISO.VALOR_INICIAL + SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES = 0   THEN SIIF_REGISTRO_COMPROMISO.VALOR_OPERACIONES
                                        ELSE 0
                                    END
                            ORDER BY
                                PLAN_PRESUPUESTAL.CODIGO
                        ) SYSMANEJECUCION
                        LEFT JOIN (SELECT
                                RUBRO,
                                SUM(VALOR_ACTUAL) VALOR_FINAL_OBLIGACIONES
                            FROM SIIF_OBLIGACIONES
                            WHERE ESTADO NOT IN ('Anulada')
                            GROUP BY RUBRO) OBLIGACIONES 
                                ON REPLACE(SYSMANEJECUCION.CODIGO,' ','') = OBLIGACIONES.RUBRO
                        LEFT JOIN (SELECT
                                    RUBRO,
                                    SUM(VALOR_PESOS - VALOR_REINTEGRADO_PESOS) PAGOS
                                FROM SIIF_ORDENESPAGO
                                WHERE ESTADO NOT IN ('Anulada')
                                GROUP BY RUBRO) ORDENDEPAGO ON ORDENDEPAGO.RUBRO = OBLIGACIONES.RUBRO
                                ORDER BY SYSMANEJECUCION.CODIGO
                ) ) SELECT
                        COMPANIA,
                        ANO,
                        CODIGO,
                        NOMBRE,
                        APROPIADO,
                        TRASLADO_DEBITO,
                        TRASLADO_CREDITO,
                        APRDEFINITIVA,
                        SUM(VLR_SOLICITUD) VLR_SOLICITUD,
                        SUM(VLR_DISPONIBILIDAD) VLR_DISPONIBILIDAD,
                        SUM(INCREMENTO_CDP) INCREMENTO_CDP,
                        SUM(DECREMENTO_CDP) DECREMENTO_CDP,
                        SUM(VALOR_FINAL_DISPONIBILIDAD) VALOR_FINAL_DISPONIBILIDAD,
                        APRDEFINITIVA - SUM(VALOR_FINAL_DISPONIBILIDAD) SALDO_DE_APROPIACION,
                        SUM(VLR_INICIAL_REGISTRO) VLR_INICIAL_REGISTRO,
                        SUM(INCREMENTO_CRP) INCREMENTO_CRP,
                        SUM(DECREMENTO_CRP) DECREMENTO_CRP,
                        SUM(VALOR_FINAL_REGISTRO) VALOR_FINAL_REGISTRO,
                        SUM(NVL(VALOR_FINAL_DISPONIBILIDAD - VALOR_FINAL_REGISTRO,0) ) SALDO_DEL_CDP,
                        VALOR_FINAL_OBLIGACIONES,
                        SUM(VALOR_FINAL_REGISTRO) - VALOR_FINAL_OBLIGACIONES SALDO_DEL_CRP,
                        PAGOS,
                        PORCENTAJE
                      FROM TMP_PRESUPUESTOANE
                    GROUP BY
                        COMPANIA,
                        ANO,
                        CODIGO,
                        NOMBRE,
                        APROPIADO,
                        TRASLADO_DEBITO,
                        TRASLADO_CREDITO,
                        APRDEFINITIVA,
                        VALOR_FINAL_OBLIGACIONES,
                        PAGOS,
                        PORCENTAJE
                    ORDER BY
                        CODIGO) 
                        SALDOS ON V_PLAN_PRESUPUESTAL.COMPANIA = SALDOS.COMPANIA
                               AND V_PLAN_PRESUPUESTAL.ANO = SALDOS.ANO
                               AND V_PLAN_PRESUPUESTAL.ID = SUBSTR(SALDOS.CODIGO,1,LENGTH(V_PLAN_PRESUPUESTAL.ID) )
                               GROUP BY
                                    V_PLAN_PRESUPUESTAL.CODIGO,
                                    V_PLAN_PRESUPUESTAL.NOMBRE
                               ORDER BY
                                    V_PLAN_PRESUPUESTAL.CODIGO
