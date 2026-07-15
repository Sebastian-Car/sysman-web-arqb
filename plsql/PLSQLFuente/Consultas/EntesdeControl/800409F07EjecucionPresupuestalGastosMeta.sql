MERGE INTO CONSULTAS FIN USING (SELECT '800409F07EjecucionPresupuestalGastosMeta' INFORME ,TO_CLOB(q'[SELECT
        CODIGO                                        Código,
        NOMBRE                                       "Descripcion",
        FUENTE                                         "Fuente De Recursos",
        APROPIACIONANTERIOR                                  "Apropiación Inicial",
        ADICION                                              "Adiciones",
        REDUCCION                                            "Reducciones",
        TRASLADO_DEBITO                                  "Credito",
        TRASLADO_CREDITO                                     "Contracréditos",
        DEFINITIVO                                           "Apropiacion Definitiva",
        COMPROMISOMES                                        "Compromisos Registro Presupuestal",
        DEFINITIVO-COMPROMISOMES                      "Saldo Por Comprometer",
        OBLIGACIONESMES                                             "Obligaciones",
        COMPROMISOMES-OBLIGACIONESMES                 "Compromisos Por Ejecutar",
        EJECUCIONMES                                         "Pagos" ,
        OBLIGACIONESMES-EJECUCIONMES                  "Obligaciones Por Pagar"
FROM(
            SELECT PLAN_PRESUPUESTAL.COMPANIA,
                   PLAN_PRESUPUESTAL.ANO,
                    PLAN_PRESUPUESTAL.CODIGO,
                    CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN PLAN_PRESUPUESTAL.NOMBRE||'/'||FUENTE_RECURSOS.NOMBRE
                        ELSE PLAN_PRESUPUESTAL.NOMBRE
                        END NOMBRE,
                   CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN SALDO_AUX_PPTAL.ID
                        ELSE PLAN_PRESUPUESTAL.CODIGO
                        END id,
                   CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN NVL(FUENTE_RECURSOS.CODIGO||'-'||FUENTE_RECURSOS.nombre,'Sin Configurar')
                        ELSE 'ND'
                        END fUENTE,
                   SUM(CASE WHEN SALDO_AUX_PPTAL.MES < s$mesInicial$s
                            THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                                      THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO
                                      ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO
                                      END + ADICION + REDUCCION
                            ELSE 0 END
                        ) AS APROPIACIONANTERIOR,
                   SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D'
                            THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO
                            ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO
                        END + ADICION + REDUCCION) AS DEFINITIVO,

              ]') || TO_CLOB(q'[     SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
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
                     SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO
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
                            THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SAL]') || TO_CLOB(q'[DO_AUX_PPTAL.EJE_PPT_CREDITO
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
                            ELSE 0 END)  SUMADEAPLAZAM_DEBITO,
                   SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                            THEN SALDO_AUX_PPTAL.APLAZAM_CREDITO
                            ELSE 0 END)  SUMADEAPLAZAM_CREDITO,
                   SUM(SALDO_AUX_PPTAL.DISPONIBILIDAD
                       )  DISPONIBILIDAD,
                   SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                            THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='C'
                                       THEN   SALDO_AUX_PPTAL.APLAZAM_DEBITO
                                       ELSE  SALDO_AUX_PPTAL.APLAZAM_CREDITO
                                 END
                       ELSE 0 END)  APLAZAMIENTO,
                   SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='C'
                                       THEN   SALDO_AUX_PPTAL.APLAZAM_DEBITO
                                       ELSE  SALDO_AUX_PPTAL.APLAZAM_CREDITO
                                       END
                                      )
                           APLAZAMIENTOACUM,
                SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
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
             FROM PLAN_PRESUPUESTAL INNER JOIN TIPOVIGENCIA 
              ON PLAN_PRESUPUESTAL.TIPOVIGENCIA = TIPOVIGENCIA.CODIGO   
             INNER JOIN SALDO_AUX_PPTAL
              ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
    ]') || TO_CLOB(q'[          AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
             --AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_AUX_PPTAL.CODIGO
              AND PLAN_PRESUPUESTAL.CODIGO       = SUBSTR(SALDO_AUX_PPTAL.CODIGO, 1, LENGTH(PLAN_PRESUPUESTAL.CODIGO))
             LEFT JOIN FUENTE_RECURSOS
                ON  SALDO_AUX_PPTAL.COMPANIA        = FUENTE_RECURSOS.COMPANIA
                AND SALDO_AUX_PPTAL.ANO              = FUENTE_RECURSOS.ANO
                AND SALDO_AUX_PPTAL.FUENTE_RECURSO   = FUENTE_RECURSOS.CODIGO
            lEfT JOIN TIPO_FUENTE_SIA
             ON FUENTE_RECURSOS.CODIGO_SIA=TIPO_FUENTE_SIA.CODIGO
             WHERE PLAN_PRESUPUESTAL.COMPANIA     = s$compania$s
              AND PLAN_PRESUPUESTAL.ANO          = s$ano$s
              AND PLAN_PRESUPUESTAL.NATURALEZA   IN('D')
              AND SALDO_AUX_PPTAL.MES <=s$mesFinal$s
              AND PLAN_PRESUPUESTAL.REGALIAS IN (0)
        AND TIPOVIGENCIA.REPORTAR_SIA <> 0
             GROUP BY PLAN_PRESUPUESTAL.COMPANIA,
                     PLAN_PRESUPUESTAL.ANO,
                     PLAN_PRESUPUESTAL.CODIGO,
                    CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN PLAN_PRESUPUESTAL.NOMBRE||'/'||FUENTE_RECURSOS.NOMBRE
                        ELSE PLAN_PRESUPUESTAL.NOMBRE
                        END,
                     CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN SALDO_AUX_PPTAL.ID
                        ELSE PLAN_PRESUPUESTAL.CODIGO
                        END,
                      CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN NVL(FUENTE_RECURSOS.CODIGO||'-'||FUENTE_RECURSOS.nombre,'Sin Configurar')
                        ELSE 'ND'
                        END
                order by compania, ano, CASE WHEN SALDO_AUX_PPTAL.CODIGO = PLAN_PRESUPUESTAL.CODIGO
                        ThEN SALDO_AUX_PPTAL.ID
                        ELSE PLAN_PRESUPUESTAL.CODIGO
                        END
 ) T]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);