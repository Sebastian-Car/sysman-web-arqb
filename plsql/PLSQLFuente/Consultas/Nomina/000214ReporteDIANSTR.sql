WITH
                    RETE_STR_DIAN 
                    AS (
                          SELECT  HISTORICOS.COMPANIA, 
                                  HISTORICOS.ID_DE_EMPLEADO, 
                                  SUM(HISTORICOS.VALOR) RETE
                          FROM HISTORICOS 
                            INNER JOIN PERIODOS 
                              ON (HISTORICOS.COMPANIA = PERIODOS.COMPANIA) 
                              AND (HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO) 
                              AND (HISTORICOS.ANO = PERIODOS.ANO) 
                              AND (HISTORICOS.MES = PERIODOS.MES) 
                              AND (HISTORICOS.PERIODO = PERIODOS.PERIODO)   
                            INNER JOIN CONCEPTOS 
                              ON (HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA)
                              AND (HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO)
                          WHERE HISTORICOS.COMPANIA = s$compania$s
                            AND HISTORICOS.ID_DE_PROCESO = s$idProceso$s
                            AND TRIM(TO_CHAR(HISTORICOS.ANO,'0000'))||TRIM(TO_CHAR(HISTORICOS.MES,'00'))||TRIM(TO_CHAR(HISTORICOS.PERIODO, '00')) BETWEEN s$rangoInicial$s  AND s$rangoFinal$s
                            AND PERIODOS.ACUMULADO NOT IN (0)
                            AND CONCEPTOS.CODDIAN = '40'
                          GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ID_DE_EMPLEADO
                    )
                      SELECT 
                        HISTORICOS.ID_DE_EMPLEADO, 
                        SUM(HISTORICOS.VALOR)  SALG, 
                        PERSONAL.DIRECCION,
                        PERSONAL.NUMERO_DCTO, 
                        RETE_STR_DIAN.RETE  RETEFUENTE, 
                        SUBSTR(PERSONAL.NOMBRES,1,INSTR(PERSONAL.NOMBRES, ' ')-1)  N1, 
                        SUBSTR(PERSONAL.NOMBRES,INSTR(PERSONAL.NOMBRES, ' ')+1, LENGTH(PERSONAL.NOMBRES))  N2, 
                        PERSONAL.APELLIDO1  P1, 
                        PERSONAL.APELLIDO2  P2, 
                        PERSONAL.NOMBRECOMPLETO
                    FROM 
                        HISTORICOS 
                     INNER JOIN PERIODOS 
                            ON (HISTORICOS.COMPANIA = PERIODOS.COMPANIA) 
                           AND (HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO) 
                           AND (HISTORICOS.ANO = PERIODOS.ANO) 
                           AND (HISTORICOS.MES = PERIODOS.MES) 
                           AND (HISTORICOS.PERIODO = PERIODOS.PERIODO)
                     INNER JOIN CONCEPTOS 
                            ON (HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA) 
                           AND (HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO)
                      INNER JOIN PERSONAL 
                            ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
                           AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
                      LEFT JOIN RETE_STR_DIAN 
                            ON (PERSONAL.COMPANIA = RETE_STR_DIAN.COMPANIA) 
                           AND (PERSONAL.ID_DE_EMPLEADO = RETE_STR_DIAN.ID_DE_EMPLEADO) 
                    WHERE
                          HISTORICOS.COMPANIA = s$compania$s
                      AND HISTORICOS.ID_DE_PROCESO = s$idProceso$s    
                      AND TRIM(TO_CHAR(HISTORICOS.ANO, '0000')) ||TRIM(TO_CHAR(HISTORICOS.MES, '00')) ||TRIM(TO_CHAR(HISTORICOS.PERIODO, '00')) BETWEEN  s$rangoInicial$s  AND s$rangoFinal$s
                      AND CONCEPTOS.CLASE IN (3)
                      AND PERIODOS.ACUMULADO NOT IN(0)
                    GROUP BY 
                          HISTORICOS.ID_DE_EMPLEADO, 
                        PERSONAL.DIRECCION,
                        PERSONAL.NUMERO_DCTO, 
                        RETE_STR_DIAN.RETE  , 
                        PERSONAL.NOMBRES  , 
                        PERSONAL.NOMBRES  , 
                        PERSONAL.APELLIDO1  , 
                        PERSONAL.APELLIDO2  , 
                        PERSONAL.NOMBRECOMPLETO
                    HAVING 
                        SUM(HISTORICOS.VALOR) > s$topeIngresosC$s
