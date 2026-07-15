SELECT  
    ANO,
    CASE TRIMESTRE 
        WHEN 1 THEN 'Primero'
        WHEN 2 THEN 'Segundo'
        WHEN 3 THEN 'Tercero'
        WHEN 4 THEN 'Cuarto'
        ELSE '0' 
    END TRIMESTRE,
    CODIGO,
    NOMBRE,
    CODIGO_ENTIDAD_RECIPROCA,
    ENTIDAD_RECIPROCA,
    VALOR_CORRIENTE,
    VALOR_NO_CORRIENTE,
    OBSERVACIONES,
    TRIMESTRE TR,
    FECHA_OBSERVACIONES
FROM SEGUIMIENTO_RECIPROCAS
WHERE COMPANIA = s$compania$s
	AND TRIMESTRE = CASE WHEN s$condicion$s NOT IN (0) 
                       THEN TRIMESTRE
                       ELSE s$trimestre$s END
	AND ANO = s$ano$s
ORDER BY TR
