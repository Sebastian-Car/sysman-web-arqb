MERGE INTO CONSULTAS FIN USING (SELECT '003000PlanillaDescuentosCDCFLORENCIA' INFORME ,TO_CLOB(q'[SELECT
    MAX(HISTORICOS.ID_DE_PROCESO)KEEP(DENSE_RANK LAST ORDER BY ROWNUM)ID_DE_PROCESO,
    MAX(HISTORICOS.ANO)KEEP(DENSE_RANK LAST ORDER BY ROWNUM)ANO,
    PCK_SYSMAN_UTL.FC_NOMBRE_MES(HISTORICOS.MES)MES,
    MAX(HISTORICOS.PERIODO)KEEP(DENSE_RANK LAST ORDER BY ROWNUM)PERIODO,
    MAX(PERSONAL.NUMERO_DCTO)AS CEDULA,
    MAX(PERSONAL.ID_DE_CARGO)AS CARGO,
    PERSONAL.NOMBRECOMPLETO   NOM_EMPLEADO,
    CENTRO_COSTO.CODIGO       ID_CENTRO_DE_COSTO,
    CENTRO_COSTO.NOMBRE       NOMBRE_CENTRO_DE_COSTO,
    SUM(CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '1'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C001,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '2'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C002,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '9'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C009,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '70'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C070,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '62'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C062,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '79'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0 END
    )AS C079,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '80'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C080,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '97'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C097,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '125'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C125,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '127'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C127,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '129'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C129,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '130'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C130,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
             ]') || TO_CLOB(q'[   '131'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C131,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '132'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C132,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '140'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C140,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '144'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C144,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '151'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C151,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '155'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C155,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '158'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C158,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '160'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C160,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '171'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C171,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '174'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C174,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '175'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C175,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '177'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C177,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '186'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C186,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '343'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C343,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '379'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C379,
    SUM(
        CASE
            WHE]') || TO_CLOB(q'[N HISTORICOS.ID_DE_CONCEPTO IN(
                '697'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C697,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '698'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C698,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '799'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C799,
    SUM(
        CASE
            WHEN HISTORICOS.ID_DE_CONCEPTO IN(
                '835'
            )THEN
                HISTORICOS.VALOR
            ELSE
                0
        END
    )AS C835,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '002'
    )AS CUENTACONCEPTO_002,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '379'
    )AS CUENTACONCEPTO_379,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '062'
    )AS CUENTACONCEPTO_062,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '079'
    )AS CUENTACONCEPTO_079,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '186'
    )AS CUENTACONCEPTO_186,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '158'
    )AS CUENTACONCEPTO_158,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '155'
    )AS CUENTACONCEPTO_155,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '160'
    )AS CUENTACONCEPTO_160,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '174'
    )AS CUENTACONCEPTO_174,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '175'
    )AS CUENTACONCEPTO_175,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '151'
    )AS CUENTACONCEPTO_151,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPA]') || TO_CLOB(q'[NIA = '001'
            AND ID_DE_CONCEPTO = '171'
    )AS CUENTACONCEPTO_171,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '070'
    )AS CUENTACONCEPTO_070,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '177'
    )AS CUENTACONCEPTO_177,
    (
        SELECT
            CTA_CRE_PPTAL
        FROM
            CONCEPTOS
        WHERE
            COMPANIA = '001'
            AND ID_DE_CONCEPTO = '080'
    )AS CUENTACONCEPTO_080
FROM
    HISTORICOS
    INNER JOIN PERIODOS ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA
                           AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                           AND HISTORICOS.ANO = PERIODOS.ANO
                           AND HISTORICOS.MES = PERIODOS.MES
                           AND HISTORICOS.PERIODO = PERIODOS.PERIODO
    INNER JOIN PERSONAL ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                           AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
    INNER JOIN CENTRO_COSTO ON PERSONAL.COMPANIA = CENTRO_COSTO.COMPANIA
                               AND PERSONAL.ID_CENTRO_DE_COSTO = CENTRO_COSTO.CODIGO
                               AND HISTORICOS.ANO = CENTRO_COSTO.ANO
WHERE
HISTORICOS.COMPANIA = s$compania$s
       AND HISTORICOS.ID_DE_PROCESO= s$proceso$s
       AND HISTORICOS.ANO = s$ano$s
       AND HISTORICOS.MES = s$mes$s
       AND HISTORICOS.PERIODO = s$periodo$s
    AND PERIODOS.ACUMULADO NOT IN(
0
)
GROUP BY
    HISTORICOS.ID_DE_PROCESO,
    HISTORICOS.ANO,
    HISTORICOS.MES,
    HISTORICOS.PERIODO,
    PERSONAL.NUMERO_DCTO,
    PERSONAL.ID_DE_CARGO,
    PERSONAL.NOMBRECOMPLETO,
    CENTRO_COSTO.CODIGO,
    CENTRO_COSTO.NOMBRE]') CONSULTA, 6 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);
COMMIT;