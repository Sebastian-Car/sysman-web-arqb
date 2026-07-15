SELECT FUENTE_RECURSOS.CODIGO_FUTCF                                                     "CONCEPTO",                      
                   SUM(CASE WHEN CUENTABANCOS.TIPO_CF NOT IN('EF','IT') 
                          OR CUENTABANCOS.TIPO_CF IS NULL
                        THEN PLAN_CONTABLE.SALDOs$mesfinal$s

                        ELSE 0
                        END)                                                            "SALDO_CAJA_BANCOS",                                                                           
                    SUM(CASE WHEN CUENTABANCOS.TIPO_CF='EF'
                        THEN PLAN_CONTABLE.SALDOs$mesfinal$s

                        ELSE 0
                        END)                                                            "SALDO_ENCARGOS_FIDUCIARIOS",                                  
                    SUM(CASE WHEN CUENTABANCOS.TIPO_CF='IT'
                        THEN PLAN_CONTABLE.SALDOs$mesfinal$s

                        ELSE 0
                          END)                                                          "INVERSIONES_TEMPORALES",                                                                      
                    SUM(PLAN_CONTABLE.SALDOs$mesfinal$s
)                                "TOTAL_DISPONIBILIDADES", 
                    NVL(RECURSOS_TERCEROS.RECURSOSTERCERO ,0)                      "RECURSOS_TERCEROS",
                    SUM( NVL(CHEQUES.CHEQUES_NO_COBRADOS,0))                            "CHEQUES_NO_COBRADOS" ,
                   NVL(RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR,0)                      "CUENTAS_POR_PAGAR_VIGENCIA",
                   NVL( RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR_ANTERIOR,0)            "CUENTAS_POR_PAGAR_VIGENCIA_ANTERIOR",
                   0                                                                    "OTRAS_EXIGIBILIDADES",
                  NVL(RESERVAS_PRESUPUESTALES.RESERVA,0)                                "RESERVAS_PRESUPUESTALES",
                  SUM( NVL(CHEQUES.CHEQUES_NO_COBRADOS,0))+NVL(RECURSOS_TERCEROS.RECURSOSTERCERO ,0)+
                    NVL(RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR,0)+
                    NVL(RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR_ANTERIOR,0)+     
                    NVL(RESERVAS_PRESUPUESTALES.RESERVA,0)                                    "SUPERAVIT_DEFICIT"
                FROM PLAN_CONTABLE INNER JOIN CUENTABANCOS 
                 ON PLAN_CONTABLE.COMPANIA     =CUENTABANCOS.COMPANIA
                   AND PLAN_CONTABLE.ANO       =CUENTABANCOS.ANO
                   AND PLAN_CONTABLE.CODIGO  =CUENTABANCOS.IDCONTABLE
            
                INNER JOIN FUENTE_RECURSOS
                  ON CUENTABANCOS.COMPANIA     =FUENTE_RECURSOS.COMPANIA
                    AND CUENTABANCOS.ANO       =FUENTE_RECURSOS.ANO
                    AND CUENTABANCOS.RECURSOS  =FUENTE_RECURSOS.CODIGO
                              
                LEFT JOIN ( SELECT PLAN_CONTABLE.COMPANIA,PLAN_CONTABLE.ANO,PLAN_CONTABLE.CODIGO,CODIGO_FUTCF,
                                    SUM(VALOR_CREDITO) CHEQUES_NO_COBRADOS
                              FROM  PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT
                                    ON PLAN_CONTABLE.COMPANIA  =DETALLE_COMPROBANTE_CNT.COMPANIA
                                   AND PLAN_CONTABLE.ANO       =DETALLE_COMPROBANTE_CNT.ANO
                                   AND PLAN_CONTABLE.CODIGO    =DETALLE_COMPROBANTE_CNT.CUENTA
                              INNER JOIN CUENTABANCOS 
                                   ON PLAN_CONTABLE.COMPANIA     =CUENTABANCOS.COMPANIA
                                     AND PLAN_CONTABLE.ANO       =CUENTABANCOS.ANO
                                     AND PLAN_CONTABLE.CODIGO  =CUENTABANCOS.IDCONTABLE    
                              INNER JOIN FUENTE_RECURSOS
                                   ON CUENTABANCOS.COMPANIA    =FUENTE_RECURSOS.COMPANIA
                                    AND CUENTABANCOS.ANO       =FUENTE_RECURSOS.ANO
                                    AND CUENTABANCOS.RECURSOS  =FUENTE_RECURSOS.CODIGO 
                              INNER JOIN TIPO_COMPROBANTE
                                   ON DETALLE_COMPROBANTE_CNT.COMPANIA  =TIPO_COMPROBANTE.COMPANIA
                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE       =TIPO_COMPROBANTE.CODIGO
                            WHERE CUENTABANCOS.COMPANIA=s$compania$s
                             AND CUENTABANCOS.ANO<=s$anioTrabajo$s
                             AND PLAN_CONTABLE.CLASECUENTA IN ('B')
                             AND TIPO_COMPROBANTE.CLASE_CONTABLE IN('E','G')
                            AND (DETALLE_COMPROBANTE_CNT.PAGADOBANCO IN(0)  AND (DETALLE_COMPROBANTE_CNT.FECHA_CONCILIA IS NULL)
                             OR (DETALLE_COMPROBANTE_CNT.FECHA_CONCILIA>LAST_DAY(TO_DATE('01/12/'||s$anioTrabajo$s,'DD/MM/YYYY'))))
                             GROUP BY PLAN_CONTABLE.COMPANIA,PLAN_CONTABLE.ANO,PLAN_CONTABLE.CODIGO,CODIGO_FUTCF)  CHEQUES
                      ON   PLAN_CONTABLE.COMPANIA= CHEQUES.COMPANIA
                      AND  PLAN_CONTABLE.ANO= CHEQUES.ANO
                      AND  PLAN_CONTABLE.CODIGO= CHEQUES.CODIGO
                      AND  FUENTE_RECURSOS.CODIGO_FUTCF=CHEQUES.CODIGO_FUTCF
                      
               LEFT JOIN (SELECT COMPANIA,ANO,CODIGO_FUT,SUM(SALDOs$mesfinal$s) RECURSOSTERCERO
                            FROM PLAN_CONTABLE
                            WHERE COMPANIA=s$compania$s
                            AND ANO=s$anioTrabajo$s
                            AND CLASECUENTA IN('I')
                            AND SALDOs$mesfinal$s>0
                            AND CODIGO_FUT IS NOT NULL
                            GROUP  BY COMPANIA,ANO,CODIGO_FUT) RECURSOS_TERCEROS
                ON   PLAN_CONTABLE.COMPANIA= RECURSOS_TERCEROS.COMPANIA
                  AND  PLAN_CONTABLE.ANO= RECURSOS_TERCEROS.ANO
                  AND  FUENTE_RECURSOS.CODIGO_FUTCF=RECURSOS_TERCEROS.CODIGO_FUT              
                      
               LEFT JOIN (SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA,
                                   DETALLE_COMPROBANTE_PPTAL.ANO,
                                   FUENTE_RECURSOS.CODIGO_FUTCF,
                                   SUM(CASE WHEN TIPO_COMPROBPP.CLASE  ='RES' AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN('VA')
                                           THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)-(DETALLE_COMPROBANTE_PPTAL.DEBITO_AFECTADO-DETALLE_COMPROBANTE_PPTAL.CREDITO_AFECTADO)+(DETALLE_COMPROBANTE_PPTAL.MODIFICACION_DEBITO-DETALLE_COMPROBANTE_PPTAL.MODIFICACION_CREDITO) 
                                           ELSE 0 END) RESERVA,
                                    SUM(CASE WHEN TIPO_COMPROBPP.CLASE  ='REO' AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN('VA')
                                           THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)-(DETALLE_COMPROBANTE_PPTAL.DEBITO_AFECTADO-DETALLE_COMPROBANTE_PPTAL.CREDITO_AFECTADO)+(DETALLE_COMPROBANTE_PPTAL.MODIFICACION_DEBITO-DETALLE_COMPROBANTE_PPTAL.MODIFICACION_CREDITO)
                                           ELSE 0 END) CUENTA_POR_PAGAR, 
                                   SUM(CASE WHEN TIPO_COMPROBPP.CLASE  ='REO' AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN('RC')
                                           THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)-(DETALLE_COMPROBANTE_PPTAL.DEBITO_AFECTADO-DETALLE_COMPROBANTE_PPTAL.CREDITO_AFECTADO)+(DETALLE_COMPROBANTE_PPTAL.MODIFICACION_DEBITO-DETALLE_COMPROBANTE_PPTAL.MODIFICACION_CREDITO)
                                           ELSE 0 END) CUENTA_POR_PAGAR_ANTERIOR             
                            FROM DETALLE_COMPROBANTE_PPTAL 
                                    INNER JOIN TIPO_COMPROBPP
                                    ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA 
                                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO 
                                   LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
                                    ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                    AND DETALLE_COMPROBANTE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                    AND DETALLE_COMPROBANTE_PPTAL.ID   = PLAN_PRESUPUESTAL.ID 
                                   LEFT JOIN FUENTE_RECURSOS 
                                      ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA=FUENTE_RECURSOS.COMPANIA
                                      AND DETALLE_COMPROBANTE_PPTAL.ANO=FUENTE_RECURSOS.ANO
                                      AND DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO=FUENTE_RECURSOS.CODIGO
                             WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = s$compania$s 
                               AND DETALLE_COMPROBANTE_PPTAL.ANO= s$anioTrabajo$s
                               AND DETALLE_COMPROBANTE_PPTAL.MES<= s$mesfinal$s
                               AND TIPO_COMPROBPP.CLASE               IN( 'RES','REO')
                              AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN('VA','RC')
                              AND FUENTE_RECURSOS.IND_SINSITUACIONFONDOS IN(0)
                              AND ROUND((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)-(DETALLE_COMPROBANTE_PPTAL.DEBITO_AFECTADO-DETALLE_COMPROBANTE_PPTAL.CREDITO_AFECTADO)+(DETALLE_COMPROBANTE_PPTAL.MODIFICACION_DEBITO-DETALLE_COMPROBANTE_PPTAL.MODIFICACION_CREDITO),2) NOT IN (0)
                            GROUP BY DETALLE_COMPROBANTE_PPTAL.COMPANIA, 
                                     DETALLE_COMPROBANTE_PPTAL.ANO,
                                     FUENTE_RECURSOS.CODIGO_FUTCF) RESERVAS_PRESUPUESTALES
                   ON   PLAN_CONTABLE.COMPANIA= RESERVAS_PRESUPUESTALES.COMPANIA
                      AND PLAN_CONTABLE.ANO= RESERVAS_PRESUPUESTALES.ANO
                      AND FUENTE_RECURSOS.CODIGO_FUTCF=RESERVAS_PRESUPUESTALES.CODIGO_FUTCF   
             WHERE CUENTABANCOS.COMPANIA=s$compania$s
             AND CUENTABANCOS.ANO=s$anioTrabajo$s
             AND PLAN_CONTABLE.CLASECUENTA IN ('B')
             AND FUENTE_RECURSOS.CODIGO_FUTCF IS NOT NULL  
             GROUP BY FUENTE_RECURSOS.CODIGO_FUTCF,
                    RECURSOS_TERCEROS.RECURSOSTERCERO,
                    RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR,
                    RESERVAS_PRESUPUESTALES.CUENTA_POR_PAGAR_ANTERIOR,
                    RESERVAS_PRESUPUESTALES.RESERVA
            ORDER BY FUENTE_RECURSOS.CODIGO_FUTCF 
