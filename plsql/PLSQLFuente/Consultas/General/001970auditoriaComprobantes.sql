SELECT ANO, TIPO,
  NUMERO,
  FECHA,
  DESCRIPCION,
  DATE_CREATED,
  CREATED_BY,
  DATE_MODIFIED
,
  MODIFIED_BY
FROM s$tabla$s
WHERE COMPANIA = s$compania$s
AND ANO BETWEEN CASE WHEN s$anio$s NOT IN (0)
                THEN TO_NUMBER(s$anoInicial$s)
                ELSE ANO 
                END 
                AND 
                CASE WHEN s$anio$s NOT IN 0
                THEN TO_NUMBER(s$anoFinal$s)
                ELSE ANO 
                END   
AND TIPO BETWEEN CASE WHEN s$tipo$s NOT IN (0)
                THEN 's$tipoInicial$s'
                ELSE TIPO 
                END 
                AND 
                CASE WHEN s$tipo$s NOT IN 0
                THEN 's$tipoFinal$s'
                ELSE TIPO 
                END 
AND NUMERO BETWEEN CASE WHEN s$comprobante$s NOT IN (0)
                THEN TO_NUMBER(s$comprobanteInicial$s)
                ELSE NUMERO 
                END 
                AND 
                CASE WHEN s$comprobante$s NOT IN 0
                THEN TO_NUMBER(s$comprobanteFinal$s)
                ELSE NUMERO 
                END
AND (CREATED_BY BETWEEN CASE WHEN s$usuario$s NOT IN (0)
                THEN 's$usuarioInicial$s'
                ELSE CREATED_BY 
                END 
                AND 
                CASE WHEN s$usuario$s NOT IN 0
                THEN 's$usuarioFinal$s'
                ELSE CREATED_BY 
                END 
    OR
    
    MODIFIED_BY BETWEEN CASE WHEN s$usuario$s NOT IN (0)
                THEN 's$usuarioInicial$s'
                ELSE MODIFIED_BY 
                END 
                AND 
                CASE WHEN s$usuario$s NOT IN 0
                THEN 's$usuarioFinal$s'
                ELSE MODIFIED_BY 
                END
    )    ORDER BY ANO,TIPO, FECHA
