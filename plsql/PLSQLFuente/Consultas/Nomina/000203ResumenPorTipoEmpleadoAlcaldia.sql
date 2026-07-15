SELECT 				   HISTORICOS.ID_DE_CONCEPTO, 
 					   CONCEPTOS.NOMBRE_CONCEPTO,
                       TIPOS_DE_EMPLEADO.NOMBRE_TIPO,
                       CASE CONCEPTOS.CLASE WHEN 3 THEN '3. VALOR DEVENGOS'
                                            WHEN 2 THEN '5. VALOR DESCUENTOS'
                                            WHEN 5 THEN '5. VALOR DESCUENTOS'
                                            WHEN 4 THEN '4. VALOR TOTAL DEVENGOS'
                                            WHEN 6 THEN '6. VALOR TOTAL DESCUENTOS'
                                            WHEN 7 THEN '7. VALOR NETO A PAGAR'
                                            WHEN 8 THEN '8. CONCEPTOS CONTABLES'
                                            WHEN 99 THEN '99. BASES'
                                            ELSE  'otros' END AS CLASECONCEPTO,
                       SUM(HISTORICOS.VALOR) AS SUMA_DE_VALOR       
                FROM PERSONAL_HISTORICO INNER JOIN HISTORICOS 
                  ON PERSONAL_HISTORICO.COMPANIA        = HISTORICOS.COMPANIA 
                 AND PERSONAL_HISTORICO.ID_DE_PROCESO   = HISTORICOS.ID_DE_PROCESO  
                 AND PERSONAL_HISTORICO.ANO             = HISTORICOS.ANO  
                 AND PERSONAL_HISTORICO.MES             = HISTORICOS.MES  
                 AND PERSONAL_HISTORICO.PERIODO         = HISTORICOS.PERIODO  
                 AND PERSONAL_HISTORICO.ID_DE_EMPLEADO  = HISTORICOS.ID_DE_EMPLEADO  
                INNER JOIN CONCEPTOS 
                  ON HISTORICOS.COMPANIA                = CONCEPTOS.COMPANIA
                 AND HISTORICOS.ID_DE_CONCEPTO          = CONCEPTOS.ID_DE_CONCEPTO 
                INNER JOIN TIPOS_DE_EMPLEADO  
                  ON PERSONAL_HISTORICO.COMPANIA        = TIPOS_DE_EMPLEADO.COMPANIA
                 AND PERSONAL_HISTORICO.ID_DE_TIPO      = TIPOS_DE_EMPLEADO.ID_DE_TIPO
                INNER JOIN PERIODOS
                  ON PERIODOS.COMPANIA        = HISTORICOS.COMPANIA 
                 AND PERIODOS.ID_DE_PROCESO   = HISTORICOS.ID_DE_PROCESO  
                 AND PERIODOS.ANO             = HISTORICOS.ANO  
                 AND PERIODOS.MES             = HISTORICOS.MES  
                 AND PERIODOS.PERIODO         = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA=s$compania$s
                  AND PERIODOS.ACUMULADO NOT IN(0)
                  AND LPAD(PERSONAL_HISTORICO.ID_DE_PROCESO,2,'0') || PERSONAL_HISTORICO.ANO || LPAD(PERSONAL_HISTORICO.MES,2,'0') || LPAD(PERSONAL_HISTORICO.PERIODO,2,'0') 
                    BETWEEN 's$desde$s' AND 's$hasta$s' 
                  s$condidconceptos$s 
                  AND CONCEPTOS.CLASE IN (8,3,5,s$conceptosdiferentes$s)
                  AND PERSONAL_HISTORICO.ID_DE_TIPO = (CASE WHEN 's$todos$s'='todos' THEN PERSONAL_HISTORICO.ID_DE_TIPO ELSE 's$todos$s' END)
                GROUP BY PERSONAL_HISTORICO.COMPANIA,
                       PERSONAL_HISTORICO.ID_DE_TIPO,
                       TIPOS_DE_EMPLEADO.NOMBRE_TIPO,
                       HISTORICOS.ID_DE_CONCEPTO, 
                       CONCEPTOS.NOMBRE_CONCEPTO,
                       CONCEPTOS.CLASE
