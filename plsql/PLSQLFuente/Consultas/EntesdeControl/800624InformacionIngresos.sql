MERGE INTO CONSULTAS FIN USING (SELECT '800624InformacionIngresos' INFORME ,TO_CLOB(q'[SELECT
    ANO.NUMERO VIGENCIA,
    CASE
        WHEN configuracion_exogena.formato = s$formato$s 
        THEN
            configuracion_exogena.concepto
        ELSE
            ''
    END concepto,
    CASE
        WHEN s$mesFinal$s = 1 THEN
            SALDO_AUX_CONTABLE.SALDO1
        WHEN s$mesFinal$s = 2 THEN
            SALDO_AUX_CONTABLE.SALDO2
        WHEN s$mesFinal$s = 3 THEN
            SALDO_AUX_CONTABLE.SALDO3
        WHEN s$mesFinal$s = 4 THEN
            SALDO_AUX_CONTABLE.SALDO4
        WHEN s$mesFinal$s = 5 THEN
            SALDO_AUX_CONTABLE.SALDO5
        WHEN s$mesFinal$s = 6 THEN
            SALDO_AUX_CONTABLE.SALDO6
        WHEN s$mesFinal$s = 7 THEN
            SALDO_AUX_CONTABLE.SALDO7
        WHEN s$mesFinal$s = 8 THEN
            SALDO_AUX_CONTABLE.SALDO8
        WHEN s$mesFinal$s = 9 THEN
            SALDO_AUX_CONTABLE.SALDO9
        WHEN s$mesFinal$s = 10 THEN
            SALDO_AUX_CONTABLE.SALDO10
        WHEN s$mesFinal$s = 11 THEN
            SALDO_AUX_CONTABLE.SALDO11
        WHEN s$mesFinal$s = 12 THEN
            SALDO_AUX_CONTABLE.SALDO12
    END SALDO
FROM
    v_plan_contable
    INNER JOIN clasecuenta ON v_plan_contable.clasecuenta = clasecuenta.codigo
    INNER JOIN configuracion_exogena ON v_plan_contable.compania = configuracion_exogena.compania
                                        AND v_plan_contable.ano = configuracion_exogena.ano
                                        AND v_plan_contable.id = configuracion_exogena.cuenta
    LEFT JOIN SALDO_AUX_CONTABLE on (SALDO_AUX_CONTABLE.compania = configuracion_exogena.compania
                                        AND SALDO_AUX_CONTABLE.ano = configuracion_exogena.ano
                                        AND SALDO_AUX_CONTABLE.codigo = configuracion_exogena.cuenta)
    LEFT JOIN ANO ON ANO.COMPANIA = configuracion_exogena.compania
WHERE
    configuracion_exogena.formato = s$formato$s
    and
    v_plan_contable.compania = s$compania$s
    AND ANO.NUMERO = s$ano$s  
GROUP BY ANO.NUMERO,
    CASE
        WHEN configuracion_exogena.formato = s$formato$s 
        THEN
            configuracion_exogena.concepto
        ELSE
            ''
    END,
    ANO.VALORUVT,
    SALDO_AUX_CONTABLE.saldo12,
    SALDO_AUX_CONTABLE.saldo11,
    SALDO_AUX_CONTABLE.SALDO1,
    SALDO_AUX_CONTABLE.SALDO2,
    SALDO_AUX_CONTABLE.SALDO3,
    SALDO_AUX_CONTABLE.SALDO4,
    SALDO_AUX_CONTABLE.SALDO5,
    SALDO_AUX_CONTABLE.SALDO6,
    SALDO_AUX_CONTABLE.SALDO7,
    SALDO_AUX_CONTABLE.SALDO8,
    SALDO_AUX_CONTABLE.SALDO9,
    SALDO_AUX_CONTABLE.SALDO10
    HAVING CASE
        WHEN s$mesFinal$s = 1 THEN
            SALDO_AUX_CONTABLE.SALDO1
        WHEN s$mesFinal$s = 2 THEN
            SALDO_AUX_CONTABLE.SALDO2
        WHEN s$mesFinal$s = 3 THEN
            SALDO_AUX_CONTABLE.SALDO3
        WHEN s$mesFinal$s = 4 THEN
            SALDO_AUX_CONTABLE.SALDO4
        WHEN s$mesFinal$s = 5 THEN
            SALDO_AUX_CONTABLE.SALDO5
        WHEN s$mesFinal$s = 6 THEN
            SALDO_]') || TO_CLOB(q'[AUX_CONTABLE.SALDO6
        WHEN s$mesFinal$s = 7 THEN
            SALDO_AUX_CONTABLE.SALDO7
        WHEN s$mesFinal$s = 8 THEN
            SALDO_AUX_CONTABLE.SALDO8
        WHEN s$mesFinal$s = 9 THEN
            SALDO_AUX_CONTABLE.SALDO9
        WHEN s$mesFinal$s = 10 THEN
            SALDO_AUX_CONTABLE.SALDO10
        WHEN s$mesFinal$s = 11 THEN
            SALDO_AUX_CONTABLE.SALDO11
        WHEN s$mesFinal$s = 12 THEN
            SALDO_AUX_CONTABLE.SALDO12
         END > (3500*ANO.VALORUVT)]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);