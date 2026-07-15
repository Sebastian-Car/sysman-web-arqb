SELECT  CUENTANOMBRE, 
        COMPANIA,
        COMPANIA.NOMBRE COMPANIANOMBRE,
        CODIGO_CUENTA,
        TRUNC(FECHA) FECHA,
        COMPROBANTE,
        TIPO_COMPROBANTE_NOM,
        TIPO_CPTE,
        TERCERO_NOM NOMBRETERCERO,
        AUXILIAR_NOM NOMBREAUXILIAR,
        CENTRO_COSTO_NOM NOMBRECENTROCOSTO,
        VALOR_DEBITO,
        VALOR_CREDITO,
        DESCRIPCION,
        CASE WHEN VALOR_DEBITO NOT IN (0)
             THEN 0
             ELSE 1
             END CAMPO1,
        CODIGO_CUENTA||CASE WHEN CENTRO_COSTO_DET IS NULL
                            THEN ''
                            ELSE CENTRO_COSTO_NOM
                            END
                     ||CASE WHEN TERCERO_DET IS NULL
                            THEN ''
                            ELSE TERCERO_NOM
                            END 
                     ||CASE WHEN AUXILIAR_DET IS NULL
                            THEN ''
                            ELSE AUXILIAR_NOM
                            END 
                      ||CUENTANOMBRE AS IDORDEN  
FROM  V_DETALLE_AUXILIAR_CNT 
   INNER JOIN COMPANIA  
     ON V_DETALLE_AUXILIAR_CNT.COMPANIA = COMPANIA.CODIGO 
WHERE V_DETALLE_AUXILIAR_CNT.COMPANIA = s$compania$s   
  AND V_DETALLE_AUXILIAR_CNT.ANO      = s$anoTrabajo$s 
  AND V_DETALLE_AUXILIAR_CNT.MES      = s$mesTrabajo$s 
  AND V_DETALLE_AUXILIAR_CNT.CODIGO_CUENTA   BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'   
  s$centroCostoCond$s
