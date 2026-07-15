SELECT DISTINCT
                     V_ACUMULADOS.ID_CENTRO_DE_COSTO CENTRO_COSTO, 
                     V_ACUMULADOS.NOMBRE_CENTRO_DE_COSTO,  
                     V_ACUMULADOS.ID_DE_CONCEPTO, 
                     V_ACUMULADOS.NOMBRE_CONCEPTO, 
                     V_ACUMULADOS.CLASE,  
                     SUM(V_ACUMULADOS.VALOR) SUMA_DE_VALOR, 
                     CASE WHEN V_ACUMULADOS.CLASE = 3 THEN 'VALOR DEVENGOS'
                     ELSE 
                        CASE WHEN V_ACUMULADOS.CLASE = 5 THEN 'VALOR DESCUENTOS' 
                        ELSE 
                            CASE WHEN V_ACUMULADOS.CLASE = 4 THEN'VALOR TOTAL DEVENGOS'
                            ELSE
                                CASE WHEN V_ACUMULADOS.CLASE = 6 THEN 'VALOR TOTAL DESCUENTOS'
                                ELSE 
                                    CASE WHEN V_ACUMULADOS.CLASE = 7 THEN 'VALOR NETO A PAGAR' 
                                    ELSE 'CONCEPTOS CONTABLES'
                                    END
                                END
                            END       
                        END
                     END  CLASECONCEPTO,  
                     SUM(CASE WHEN V_ACUMULADOS.CLASE = 3 OR V_ACUMULADOS.CLASE = 8 THEN 
                            V_ACUMULADOS.VALOR
                         ELSE
                            0 
                         END)  VALORNOMINA 
                     FROM V_ACUMULADOS
                     WHERE V_ACUMULADOS.COMPANIA = s$compania$s
                       AND (LPAD(ID_DE_PROCESO,2,0)|| 
                            LPAD(V_ACUMULADOS.ANO,4,0) ||
                            LPAD(V_ACUMULADOS.MES,2,0) ||
                            LPAD(V_ACUMULADOS.PERIODO, 2,0))
                            
                            BETWEEN LPAD(s$proceso$s,2,0)|| 
                                    LPAD(s$ano1$s,4,0)||
                                    LPAD (s$mes1$s,2,0) ||
                                    LPAD (s$periodo1$s,2,0) 
                                AND LPAD(s$proceso$s,2,0)||
                                    LPAD(s$anio2$s,4,0)|| 
                                    LPAD(s$mes2$s,2,0)||
                                    LPAD(s$periodo2$s,2,0) 
                      GROUP BY 
                                 V_ACUMULADOS.ID_CENTRO_DE_COSTO, 
                                 V_ACUMULADOS.NOMBRE_CENTRO_DE_COSTO, 
                                 V_ACUMULADOS.ID_DE_CONCEPTO, 
                                 V_ACUMULADOS.NOMBRE_CONCEPTO,
                                 V_ACUMULADOS.CLASE,  
                                 CASE WHEN V_ACUMULADOS.CLASE = 3 THEN 'VALOR DEVENGOS' 
                                 ELSE 
                                    CASE WHEN V_ACUMULADOS.CLASE = 5 THEN 'VALOR DESCUENTOS' 
                                    ELSE
                                       CASE WHEN V_ACUMULADOS.CLASE = 4 THEN 'VALOR TOTAL DEVENGOS'
                                       ELSE
                                          CASE WHEN V_ACUMULADOS.CLASE = 6 THEN 'VALOR TOTAL DESCUENTOS' 
                                          ELSE
                                             CASE WHEN V_ACUMULADOS.CLASE = 7 THEN 'VALOR NETO A PAGAR'
                                             ELSE 'CONCEPTOS CONTABLES'
                                             END
                                          END
                                       END
                                    END
                                 END,
                                 V_ACUMULADOS.ID_CENTRO_DE_COSTO
                                 HAVING (V_ACUMULADOS.CLASE = 3
                                 OR     V_ACUMULADOS.CLASE = 4 
                                 OR     V_ACUMULADOS.CLASE = 5 
                                 OR     V_ACUMULADOS.CLASE = 6 
                                 OR     V_ACUMULADOS.CLASE = 7 
                                 OR     V_ACUMULADOS.CLASE = 8 )
                                 ORDER BY V_ACUMULADOS.ID_DE_CONCEPTO  
