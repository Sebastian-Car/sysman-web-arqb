    SELECT
        DEVOLUTIVO.COMPANIA,
        SUBSTR(ELEMENTO,0,3)  EXPR1,
        DEVOLUTIVO.DEPENDENCIA,
        I.NOMBRELARGO,
        SUM(DEVOLUTIVO.VALOR) SUMADEVALOR
    FROM DEVOLUTIVO
    INNER JOIN INVENTARIO I
    ON DEVOLUTIVO.COMPANIA = I.COMPANIA
    AND SUBSTR (DEVOLUTIVO.ELEMENTO,0,3) = I.CODIGOELEMENTO
    WHERE DEVOLUTIVO.COMPANIA = $P{PR_COMPANIA}
    AND DEVOLUTIVO.DEPENDENCIA = $P{PR_DEPENDENCIA}
    GROUP BY
        DEVOLUTIVO.COMPANIA,
        SUBSTR(ELEMENTO,0,3),
        I.NOMBRELARGO,                                                                                                                                                                                                                     
        DEVOLUTIVO.DEPENDENCIA
    ORDER BY
        DEVOLUTIVO.COMPANIA,
        SUBSTR(ELEMENTO,0,3)
