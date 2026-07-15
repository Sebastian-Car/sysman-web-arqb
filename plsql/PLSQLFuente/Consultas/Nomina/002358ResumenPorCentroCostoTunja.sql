MERGE INTO CONSULTAS FIN USING (SELECT '002358ResumenPorCentroCostoTunja' INFORME ,TO_CLOB(q'[SELECT DISTINCT
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
                                       CASE WHEN V_ACUMULADOS.CLASE = 4 THEN 'VALOR TOTAL]') || TO_CLOB(q'[ DEVENGOS'
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
                                 ORDER BY V_ACUMULADOS.ID_DE_CONCEPTO  ]') CONSULTA, 6 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);