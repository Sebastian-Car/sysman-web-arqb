MERGE INTO CONSULTAS FIN USING (SELECT '800596EjecucionPresupuestalGastos' INFORME ,TO_CLOB(q'[SELECT  SALDOS.CODIGO CUENTA,
            SALDOS.NOMBRE,
            SALDOS.ANO,
            SALDOS.NOMBRE_FUENTE,
            'No Aplica' AS SECTOR_INCIDENCIA_CUENTA,
             SALDOS.TIPO_GASTO,
            SUM(SALDOS.APROPIACIONINICIAL) APROPIACION_INICIAL, 
            SUM(SALDOS.ADICION - SALDOS.REDUCCION + (SALDOS.TRASLADO_DEBITO - SALDOS.TRASLADO_CREDITO) -(SALDOS.APLAZAM_CREDITO + SALDOS.APLAZAM_DEBITO)) AS MODIFICACIONES_MES,
            SUM(SALDOS.ADICIONACUMULADA - SALDOS.REDUCCIONCUMULADA + (SALDOS.TRASLADO_DEBITO_ACUM - SALDOS.TRASLADO_CREDITO_ACUM)  + SALDOS.APLAZAMIENTOACUM) AS MODIFICACIONES_ACUMULADAS,
            SUM(SALDOS.DEFINITIVO) APROPIACION_VIGENTE,
            SUM(SALDOS.APLAZAMIENTOACUM) AS SUSPENCION_DE_APROPIACION,
            SUM(SALDOS.DEFINITIVO - SALDOS.APLAZAMIENTOACUM) AS APROPIACION_DISPONIBLE,
            SUM(COMPROMISOMES) COMPROMISOS_MES,
            SUM(COMPROMISO) COMPROMISOS_ACUMULADOS, 
            SUM(CASE WHEN SALDOS.DEFINITIVO - SALDOS.APLAZAMIENTOACUM = 0
            THEN 0 
            ELSE            
            PCK_SYSMAN_UTL.FC_ROUND((COMPROMISO / (SALDOS.DEFINITIVO - SALDOS.APLAZAMIENTOACUM) * 100),2) END)
            PORC_EJECUCION,
            SUM(EJECUCIONMES) PAGOS_MES,
            SUM(EJECUCION) PAGOS_ACUMULADOS,
            SUM(CASE WHEN SALDOS.DEFINITIVO - SALDOS.APLAZAMIENTOACUM = 0
            THEN 0 
            ELSE            
            PCK_SYSMAN_UTL.FC_ROUND((EJECUCION / (SALDOS.DEFINITIVO - SALDOS.APLAZAMIENTOACUM) * 100),2) END)  PORC_EJECUCION_GIROS,
            'No Aplica' AS CODIGO_AREA_FUNCIONAL,
            'No Aplica' AS NOMBRE_AREA_FUNCIONAL,
            '0' AS CODIGO_SECTOR,
            '0' AS NOMBRE_SECTOR,
            ORD.CEDULA   AS DOC_ORD_GASTO,
            SUBSTR(ORD.NOMBRE, 1, INSTR(ORD.NOMBRE, ' ') - 1) AS PRIMER_NOMBRE,
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ') + 1, INSTR(ORD.NOMBRE, ' ', 1, 2) - INSTR(ORD.NOMBRE, ' ') - 1) AS SEGUNDO_NOMBRE
            ,
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ', 1, 2) + 1, INSTR(ORD.NOMBRE, ' ', 1, 3) - INSTR(ORD.NOMBRE, ' ', 1, 2) - 1)
            AS PRIMER_APELLIDO,
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ', 1, 3) + 1) AS SEGUNDO_APELLIDO,
            NULL AS ORIGEN_PPTO_MIXTO_EMP,
            NULL AS ORIGEN_PPTO_MIXTO_DEP,
            NULL AS ORIGEN_PPTO_MIXTO_MUN,
            NULL AS ORIGEN_PPTO_MIXTO_NACION,
            NULL AS ORIGEN_PPTO_MIXTO_NACIONSGP,
            NULL AS ORIGEN_PPTO_MIXTO_NACIONSGR,
            NULL AS ORIGEN_PPTO_MIXTO_PROP
FROM  ORDENADOR ORD INNER JOIN (
                SELECT PLAN_PRESUPUESTAL.COMPANIA,
                       PLAN_PRESUPUESTAL.ANO,
                       SALDO_AUX_PPTAL.CODIGO,
                       PLAN_PRESUPUESTAL.NOMBRE,
                       FUENTE_RECURSOS.NOMBRE NOMBRE_FUENTE,
                       SALDO_AUX_PPTAL.FUENTE_RECURSO,
                        CASE
                            WHEN PLAN_PRESUPUESTAL.DESTINO = 'F' THEN
    ]') || TO_CLOB(q'[                            'FUNCIONAMIENTO'
                            WHEN PLAN_PRESUPUESTAL.DESTINO = 'I' THEN
                                'INVERSION'
                            ELSE
                                'SERVICIO A LA DEUDA'
                        END AS TIPO_GASTO,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES < s$mesInicial$s
                                THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                          THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                                          ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
                                          END + ADICION + REDUCCION
                                ELSE 0 END
                            ) AS APROPIACIONINICIAL,
                       SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                                ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
                            END + ADICION + REDUCCION) AS DEFINITIVO,  

                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                                THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                          THEN   APROPIACION_DEBITO - APROPIACION_CREDITO 
                                          ELSE - APROPIACION_DEBITO + APROPIACION_CREDITO 
                                          END
                                ELSE 0 END) APROPIADO,                
                       SUM(SALDO_AUX_PPTAL.REDUCCION*-1) AS REDUCCIONCUMULADA,          
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                                THEN SALDO_AUX_PPTAL.REDUCCION*-1 
                                ELSE 0 END)  REDUCCION,                
                      SUM(SALDO_AUX_PPTAL.ADICION 
                           ) AS ADICIONACUMULADA,          
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s 
                                THEN SALDO_AUX_PPTAL.ADICION 
                                ELSE 0 END)  ADICION,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s 
                                THEN SALDO_AUX_PPTAL.TRASLADO_DEBITO 
                                ELSE 0 END)  TRASLADO_DEBITO,
                        SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO 
                           )  TRASLADO_DEBITO_ACUM,         
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s 
                                THEN SALDO_AUX_PPTAL.TRASLADO_CREDITO 
                                ELSE 0 END)  TRASLADO_CREDITO, 
                         SUM(SALDO_]') || TO_CLOB(q'[AUX_PPTAL.TRASLADO_CREDITO 
                               )  TRASLADO_CREDITO_ACUM,          
                       SUM(SALDO_AUX_PPTAL.REG_CONTRACT 
                               + SALDO_AUX_PPTAL.REG_NO_CONTRACT
                               + SALDO_AUX_PPTAL.MODIF_REG_CONT
                               + SALDO_AUX_PPTAL.MODIF_REG_NOCONT
                            )  COMPROMISO,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                               SALDO_AUX_PPTAL.REG_CONTRACT 
                               + SALDO_AUX_PPTAL.REG_NO_CONTRACT
                               + SALDO_AUX_PPTAL.MODIF_REG_CONT
                               + SALDO_AUX_PPTAL.MODIF_REG_NOCONT
                            ELSE 0 END  
                            )  COMPROMISOMES,   
                       SUM(SALDO_AUX_PPTAL.REGISTRO_OBLIGACION
                               + SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACION
                          )  OBLIGACIONES,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                               SALDO_AUX_PPTAL.REGISTRO_OBLIGACION
                               + SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACION
                            ELSE 0 END  
                            ) OBLIGACIONESMES,         
                       SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                                ELSE - SALDO_AUX_PPTAL.EJE_PPT_DEBITO + SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                                END 
                          )  EJECUCION,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                                CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                                THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                                ELSE - SALDO_AUX_PPTAL.EJE_PPT_DEBITO + SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                                END 
                            ELSE 0
                            END
                          )  EJECUCIONMES, 
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                                THEN SALDO_AUX_PPTAL.APLAZAM_DEBITO
                                ELSE 0 END) APLAZAM_DEBITO,
                       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                                THEN SALDO_AUX_PPTAL.APLAZAM_CREDITO
                                ELSE 0 END)  APLAZAM_CREDITO,
                       SUM(SALDO_AUX_PPTAL.DISPONIBILIDAD
                           )  DISPONIBILIDAD,

                       SUM( SALDO_AUX_PPTAL.APLAZAM_CREDITO - SALDO_AUX_PPTAL.APLAZAM_DEBITO ) APLAZAMIENTOACUM, 


                    SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInic]') || TO_CLOB(q'[ial$s AND  s$mesFinal$s 
                                THEN (CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='C' 
                                           THEN   SALDO_AUX_PPTAL.APLAZAM_CREDITO 
                                           ELSE  SALDO_AUX_PPTAL.APLAZAM_DEBITO
                                           END
                                          ) 
                                ELSE 0 END)  DESAPLAZAMIENTO,
                       SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='C' 
                                THEN   SALDO_AUX_PPTAL.APLAZAM_CREDITO
                                ELSE  SALDO_AUX_PPTAL.APLAZAM_DEBITO
                          END) DESAPLAZAMIENTOACUM             

                 FROM PLAN_PRESUPUESTAL INNER JOIN SALDO_AUX_PPTAL
                      ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA 
                     AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
                     AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_AUX_PPTAL.CODIGO
                 INNER JOIN FUENTE_RECURSOS
                    ON  FUENTE_RECURSOS.COMPANIA = SALDO_AUX_PPTAL.COMPANIA 
                     AND FUENTE_RECURSOS.ANO      = SALDO_AUX_PPTAL.ANO
                     AND FUENTE_RECURSOS.CODIGO   = SALDO_AUX_PPTAL.FUENTE_RECURSO
                 WHERE PLAN_PRESUPUESTAL.COMPANIA     = s$compania$s
                  AND PLAN_PRESUPUESTAL.ANO          = s$ano$s 
                  AND PLAN_PRESUPUESTAL.NATURALEZA   IN('D')
                  AND PLAN_PRESUPUESTAL.TIPOVIGENCIA NOT IN ('RC')
                  AND SALDO_AUX_PPTAL.MES <=s$mesFinal$s
                  AND PLAN_PRESUPUESTAL.REGALIAS IN (0) 
                 GROUP BY PLAN_PRESUPUESTAL.COMPANIA,
                         PLAN_PRESUPUESTAL.ANO,
                         SALDO_AUX_PPTAL.CODIGO,
                         SALDO_AUX_PPTAL.FUENTE_RECURSO,
                         PLAN_PRESUPUESTAL.NOMBRE,
                       FUENTE_RECURSOS.NOMBRE,
                       CASE
                            WHEN PLAN_PRESUPUESTAL.DESTINO = 'F' THEN
                                'FUNCIONAMIENTO'
                            WHEN PLAN_PRESUPUESTAL.DESTINO = 'I' THEN
                                'INVERSION'
                            ELSE
                                'SERVICIO A LA DEUDA'
                        END
                ) SALDOS 
        ON ORD.COMPANIA=SALDOS.COMPANIA
           AND ORD.IND_ACTIVOCON=-1
GROUP BY   SALDOS.CODIGO,
            SALDOS.NOMBRE,
            SALDOS.ANO,
            SALDOS.NOMBRE_FUENTE,
            'No Aplica',
           SALDOS.TIPO_GASTO,
          'No Aplica' ,
            'No Aplica',
            '0' ,
            '0',
            ORD.CEDULA,
            SUBSTR(ORD.NOMBRE, 1, INSTR(ORD.NOMBRE, ' ') - 1),
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ') + 1, INSTR(ORD.NOMBRE, ' ', 1, 2) - INSTR(ORD.NOMBRE, ' ') - 1),
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ', 1, 2) + 1, INSTR(ORD.NOMBRE, ' ', 1, 3) - INSTR(ORD.NOMBR]') || TO_CLOB(q'[E, ' ', 1, 2) - 1),
            SUBSTR(ORD.NOMBRE, INSTR(ORD.NOMBRE, ' ', 1, 3) + 1) ,
            NULL, 
            NULL,
            NULL,
            NULL ,
            NULL ,
            NULL ,
            NULL      
ORDER BY SALDOS.CODIGO]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);