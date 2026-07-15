SELECT DISTINCT     CUOTASPARTES_DETALLE.COMPANIA,
                    CUOTASPARTES_DETALLE.NIT, 
                    CUOTASPARTES_DETALLE.PORCENTAJE, 
                    TERCERO.NOMBRE NOMBREENTIDAD, 
                    PERSONAL.NOMBRECOMPLETO  NOMBRECOMPLETO, 
                    HISTORICOS.ID_DE_EMPLEADO, 
                    SUM(HISTORICOS.VALOR)  SUMATORIA, 
                    PERSONAL.NUMERO_DCTO, 
                    SUM(CASE WHEN 
                            HISTORICOS.ID_DE_CONCEPTO = 2
                        THEN 
                            HISTORICOS.VALOR
                        ELSE
                            0
                        END + CASE WHEN 
                                   HISTORICOS.ID_DE_CONCEPTO = 552
                               THEN 
                                   HISTORICOS.VALOR
                               ELSE
                                   0
                               END)  TOTALMESADAS, 
                    SUM(CASE WHEN 
                            HISTORICOS.ID_DE_CONCEPTO = 553
                        THEN 
                            HISTORICOS.VALOR
                        ELSE
                            0
                        END)  REAJUSTEMESADAS, 
                    ROUND(SUM((CASE WHEN 
                                       HISTORICOS.ID_DE_CONCEPTO = 2
                                   THEN 
                                       HISTORICOS.VALOR
                                   ELSE
                                       0
                                   END + CASE WHEN 
                                                         HISTORICOS.ID_DE_CONCEPTO = 552
                                                     THEN 
                                                         HISTORICOS.VALOR
                                                     ELSE
                                                         0
                                                     END)) * PORCENTAJE / 100, 0)  PORCENTAJEMESADAS, 
                    CUOTASPARTES_DETALLE.SALDOINICIAL, 
                    CUOTASPARTES_DETALLE.ABONO, 
                    CUOTASPARTES_DETALLE.FECHAABONO  
FROM CUOTASPARTES_DETALLE 
  LEFT JOIN HISTORICOS 
    ON  CUOTASPARTES_DETALLE.COMPANIA       = HISTORICOS.COMPANIA 
    AND CUOTASPARTES_DETALLE.ID_DE_EMPLEADO = HISTORICOS.ID_DE_EMPLEADO 
  LEFT JOIN CONCEPTOS 
    ON  HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA 
    AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
  LEFT JOIN PERIODOS 
    ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
    AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
    AND HISTORICOS.ANO           = PERIODOS.ANO 
    AND HISTORICOS.MES           = PERIODOS.MES 
    AND HISTORICOS.PERIODO       = PERIODOS.PERIODO 
  LEFT JOIN CUOTASPARTES 
    ON  CUOTASPARTES_DETALLE.COMPANIA = CUOTASPARTES.COMPANIA 
    AND CUOTASPARTES_DETALLE.NIT      = CUOTASPARTES.NIT   
    AND CUOTASPARTES_DETALLE.SUCURSAL = CUOTASPARTES.SUCURSAL 
  LEFT JOIN TERCERO            
    ON  CUOTASPARTES_DETALLE.COMPANIA = TERCERO.COMPANIA  
    AND CUOTASPARTES_DETALLE.NIT      = TERCERO.NIT
    AND CUOTASPARTES_DETALLE.SUCURSAL = TERCERO.SUCURSAL 
  LEFT JOIN PERSONAL  
    ON  HISTORICOS.COMPANIA       = PERSONAL.COMPANIA 
    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
WHERE CUOTASPARTES_DETALLE.COMPANIA = s$compania$s   
     AND HISTORICOS.ID_DE_PROCESO || HISTORICOS.ANO || LPAD(HISTORICOS.MES, 2, '0') || LPAD(HISTORICOS.PERIODO, 2, '0') BETWEEN  's$perInicial$s '
                                                                                          AND  's$perFinal$s'
  AND PERIODOS.ACUMULADO        NOT IN (0)  
  AND HISTORICOS.ID_DE_CONCEPTO IN (2,552,553)  
  s$strWhere$s
GROUP BY CUOTASPARTES_DETALLE.COMPANIA, 
         CUOTASPARTES_DETALLE.NIT, 
         CUOTASPARTES_DETALLE.PORCENTAJE, 
         TERCERO.NOMBRE, 
         PERSONAL.NOMBRECOMPLETO, 
         HISTORICOS.ID_DE_EMPLEADO, PERSONAL.NUMERO_DCTO, CUOTASPARTES_DETALLE.SALDOINICIAL, CUOTASPARTES_DETALLE.ABONO, 
CUOTASPARTES_DETALLE.FECHAABONO
