MERGE INTO CONSULTAS FIN USING (SELECT '800453PROGRAMACION_DE_GASTOS_T1' INFORME ,TO_CLOB(q'[SELECT
    CASE
        WHEN SUBSTR(CODIGO, 1, 3) = '2.3' THEN
            '2.99'
        ELSE
            CODIGO
    END CODIGO
,
    s$nombre$s
    VIGENCIA_GASTO,
    SECCION_PRESUPUESTAL,
    SECTOR,
    SUM(APROPIACION_INICIAL) APROPIACION_INICIAL,
    SUM(APROPIACION_DEFINITIVA) APROPIACION_DEFINITIVA
FROM
    (
        SELECT
            NVL(INI.CODIGOCCPET_MAN, INI.CODIGOCCPET_PAD) CODIGO,       
            (SELECT NOMBRE FROM TIPOCLASIFICADOR WHERE CODIGO = NVL(INI.CODIGOCCPET_MAN, INI.CODIGOCCPET_PAD) AND ANO = s$anio$s AND COMPANIA = s$compania$s AND ROWNUM =1) NOMBRE,
            INI.VIGENCIA_GASTO,
            NVL(NVL(INI.CODIGOUNIDADEJE_MAN, INI.CODIGOUNIDADEJE_PAD), 0) SECCION_PRESUPUESTAL,
            NVL(INI.SECTOR, 0) SECTOR,
            SUM(SALDO.APROPIACION_DEBITO) APROPIACION_INICIAL,
            SUM(SALDO.APROPIACION_DEBITO + SALDO.ADICION + SALDO.REDUCCION +(SALDO.TRASLADO_DEBITO - SALDO.TRASLADO_CREDITO) +(SALDO
            .APLAZAM_DEBITO - SALDO.APLAZAM_CREDITO)) APROPIACION_DEFINITIVA
        FROM
            (
                SELECT
                    MOV.COMPANIA,
                    MOV.ANO,
                    MOV.CODIGO,
                    MAX(
                        CASE
                            WHEN PADRE.CLASECLASIFICADOR = '006' THEN
                                PADRE.TIPOCLASIFICADOR
                            ELSE
                                NULL
                        END
                    ) CODIGOCCPET_PAD,
                    MOV.CODIGOCCPET        CODIGOCCPET_MAN,
                    MAX(
                        CASE
                            WHEN PADRE.CLASECLASIFICADOR = '008' THEN
                                PADRE.TIPOCLASIFICADOR
                            ELSE
                                NULL
                        END
                    ) CODIGOUNIDADEJE_PAD,
                    MOV.CODIGOUNIDADEJE    CODIGOUNIDADEJE_MAN,
                    MOV.TIPOVIGENCIA,
                    MOV.SECTOR,
                    TV.EQUIVALENTE_CUIPO   VIGENCIA_GASTO
                FROM
                    PLAN_PRESUPUESTAL   MOV
                    LEFT JOIN PLAN_PRESUPUESTAL   PADRE ON PADRE.COMPANIA = MOV.COMPANIA
                                                                           AND PADRE.ANO = MOV.ANO
                                                                           AND PADRE.CODIGO = SUBSTR(MOV.CODIGO, 1, LENGTH(PADRE.
                                                                           CODIGO))
                    INNER JOIN TIPOVIGENCIA        TV ON TV.CODIGO = MOV.TIPOVIGENCIA
                    
                WHERE
                    MOV.COMPANIA = s$compania$s  
                    AND MOV.ANO = s$anio$s  
                    AND MOV.NATURALEZA = 'D'
                    AND MOV.REGALIAS IN (
                        0
                    )
                    AND ( MOV.MOVIMIENTO <> 0
 ]') || TO_CLOB(q'[                         OR MOV.MAN_AUX_FUE <> 0
                          OR MOV.MAN_AUX_GEN <> 0
                          OR MOV.MAN_AUX_REF <> 0
                          OR MOV.MAN_AUX_TER <> 0
                          OR MOV.MAN_CEN_CTO <> 0 )
                GROUP BY
                    MOV.COMPANIA,
                    MOV.ANO,
                    MOV.CODIGO,
                    MOV.PROGRAMA,
                    MOV.CODIGOUNIDADEJE,
                    MOV.CODIGOCCPET,
                    MOV.TIPOVIGENCIA,
                    MOV.SECTOR,
                    TV.EQUIVALENTE_CUIPO
            ) INI
            INNER JOIN SALDO_AUX_PPTAL SALDO ON INI.COMPANIA = SALDO.COMPANIA
                                                                  AND INI.ANO = SALDO.ANO
                                                                  AND INI.CODIGO = SALDO.CODIGO
        WHERE
            SALDO.COMPANIA = s$compania$s      
            AND SALDO.ANO = s$anio$s      
            AND SALDO.MES <= s$mesfinal$s 
            AND NVL(INI.CODIGOCCPET_MAN, INI.CODIGOCCPET_PAD) IS NOT NULL
        GROUP BY
            NVL(INI.CODIGOCCPET_MAN, INI.CODIGOCCPET_PAD),
            INI.VIGENCIA_GASTO,
            INI.SECTOR,
            NVL(INI.CODIGOUNIDADEJE_MAN, INI.CODIGOUNIDADEJE_PAD)
        HAVING SUM(SALDO.APROPIACION_DEBITO) <> 0
               OR SUM(SALDO.APROPIACION_DEBITO + SALDO.ADICION + SALDO.REDUCCION +(SALDO.TRASLADO_DEBITO - SALDO.TRASLADO_CREDITO
               ) +(SALDO.APLAZAM_DEBITO - SALDO.APLAZAM_CREDITO)) <> 0
    ) CUIPO
GROUP BY
        CASE
            WHEN SUBSTR(CODIGO, 1, 3) = '2.3' THEN
                '2.99'
            ELSE
                CODIGO
        END,
        s$nombre$s
        VIGENCIA_GASTO,
        SECCION_PRESUPUESTAL,
        SECTOR
ORDER BY
    CODIGO]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);