WITH CONSULTAVAL AS
        (SELECT CODIGOSFUT.CODIGOFUT AS CODIGO,
                V_RESUMENPPTO_BASE.ID,
                CODIGOSFUT.NOMBRE,
                CODIGOSFUT.DESCRIPCION,
                PLAN_PPTAL_CONFIG.FUENTE_FUT,
                FUENTESFUT.NOMBREFU_FUT NOMBREFUENTEFUT,
                CASE WHEN s$miles$s = 0
                  THEN V_RESUMENPPTO_BASE.APROPIADO
                  ELSE PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR     => V_RESUMENPPTO_BASE.APROPIADO /1000 , 
                                                UN_PRECISION => s$digitoR$s)
                END PRESUPUESTO_INICIAL,
                CASE WHEN s$miles$s = 0 THEN
                    CASE WHEN V_RESUMENPPTO_BASE.MES = 0
                      THEN  V_RESUMENPPTO_BASE.APROPIADO + 
                            V_RESUMENPPTO_BASE.ADICION   + 
                            V_RESUMENPPTO_BASE.REDUCCION + 
                            V_RESUMENPPTO_BASE.TRASLADO  -
                        CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => s$compania$s
                                                                          ,UN_NOMBRE    => 'VIGENCIAS INCLUIDAS EN GASTOS DE INVERSION FUT'
                                                                          ,UN_MODULO    => 99
                                                                          ,UN_FECHA_PAR => SYSDATE),'NO') = 'SI'
                          THEN V_RESUMENPPTO_BASE.APLAZAMIENTO
                          ELSE 0
                        END
                    ELSE 0
                    END
                ELSE CASE WHEN V_RESUMENPPTO_BASE.MES = 0
                        THEN PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR => ( V_RESUMENPPTO_BASE.APROPIADO + 
                                                                    V_RESUMENPPTO_BASE.ADICION   + 
                                                                    V_RESUMENPPTO_BASE.REDUCCION + 
                                                                    V_RESUMENPPTO_BASE.TRASLADO/1000 ), 
                                                      UN_PRECISION => s$digitoR$s)
                        ELSE 0
                     END
                END PRESUPUESTO_DEFINITIVO,
                CASE WHEN s$miles$s = 0
                    THEN V_RESUMENPPTO_BASE.TOTALREGCONT + 
                         V_RESUMENPPTO_BASE.TOTALREGNOCONTRACT
                    ELSE PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR => (V_RESUMENPPTO_BASE.TOTALREGCONT + 
                                                               V_RESUMENPPTO_BASE.TOTALREGNOCONTRACT )/1000 , 
                                                  UN_PRECISION => s$digitoR$s)
                END COMPROMISOS,
                CASE
                    WHEN s$miles$s = 0 THEN V_RESUMENPPTO_BASE.REO + 
                                           V_RESUMENPPTO_BASE.MODIFREO
                    ELSE PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR => (V_RESUMENPPTO_BASE.REO + 
                                                               V_RESUMENPPTO_BASE.MODIFREO )/1000 , 
                                                  UN_PRECISION => s$digitoR$s)
                END OBLIGACIONES,
                CASE WHEN s$miles$s = 0
                    THEN V_RESUMENPPTO_BASE.EJECUCIONPPT + 
                         V_RESUMENPPTO_BASE.REINTEGRO
                    ELSE PCK_SYSMAN_UTL.FC_ROUND( UN_VALOR => ( V_RESUMENPPTO_BASE.EJECUCIONPPT + 
                                                                V_RESUMENPPTO_BASE.REINTEGRO )/1000 , 
                                                  UN_PRECISION => s$digitoR$s)
                END PAGOS
       FROM V_PLAN_PRESUPUESTAL
      INNER JOIN PLAN_PPTAL_CONFIG
         ON PLAN_PPTAL_CONFIG.COMPANIA      = V_PLAN_PRESUPUESTAL.COMPANIA
        AND PLAN_PPTAL_CONFIG.ANO           = V_PLAN_PRESUPUESTAL.ANO
        AND PLAN_PPTAL_CONFIG.CODIGO        = V_PLAN_PRESUPUESTAL.CODIGO
        AND PLAN_PPTAL_CONFIG.CENTRO_COSTO  = V_PLAN_PRESUPUESTAL.CENTRO_COSTO
        AND PLAN_PPTAL_CONFIG.AUXILIAR      = V_PLAN_PRESUPUESTAL.AUXILIAR
        AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO= V_PLAN_PRESUPUESTAL.FUENTE_RECURSO
      INNER JOIN V_RESUMENPPTO_BASE
         ON V_PLAN_PRESUPUESTAL.COMPANIA    = V_RESUMENPPTO_BASE.COMPANIA
        AND V_PLAN_PRESUPUESTAL.ANO         = V_RESUMENPPTO_BASE.ANO
        AND V_PLAN_PRESUPUESTAL.CODIGO      = V_RESUMENPPTO_BASE.CODIGO
      INNER JOIN CODIGOSFUT
         ON PLAN_PPTAL_CONFIG.COMPANIA      = CODIGOSFUT.COMPANIA
        AND PLAN_PPTAL_CONFIG.ANO           = CODIGOSFUT.ANO
        AND PLAN_PPTAL_CONFIG.CONSECUTIVO_FUT= CODIGOSFUT.CODIGOFUT
       LEFT JOIN FUENTESFUT
         ON PLAN_PPTAL_CONFIG.COMPANIA = FUENTESFUT.COMPANIA
        AND PLAN_PPTAL_CONFIG.ANO = FUENTESFUT.ANO
        AND PLAN_PPTAL_CONFIG.FUENTE_FUT = FUENTESFUT.CODIGOFU_FUT
        AND PLAN_PPTAL_CONFIG.DESTINO = FUENTESFUT.DESTINO
      WHERE V_PLAN_PRESUPUESTAL.COMPANIA    = s$compania$s
        AND V_PLAN_PRESUPUESTAL.ANO         = s$ano$s
        AND V_RESUMENPPTO_BASE.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
        AND V_RESUMENPPTO_BASE.DESTINO       IN ('I')
        AND V_RESUMENPPTO_BASE.NATURALEZA    IN ('D')
        AND V_PLAN_PRESUPUESTAL.FUENTE_FUT   IS NOT NULL
        AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA NOT IN ('RC','RA')
        AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA IN (NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => s$compania$s
                                                                          ,UN_NOMBRE    => 'VIGENCIAS INCLUIDAS EN GASTOS DE INVERSION FUT'
                                                                          ,UN_MODULO    => 99
                                                                          ,UN_FECHA_PAR => SYSDATE),'VA')))
     SELECT CODIGO,
            NOMBRE,
            ID,
            FUENTE_FUT,
            NOMBREFUENTEFUT,
            DESCRIPCION,
            PRESUPUESTO_INICIAL,
            PRESUPUESTO_DEFINITIVO,
            COMPROMISOS,
            OBLIGACIONES,
            PAGOS
       FROM CONSULTAVAL
      WHERE PRESUPUESTO_INICIAL    + 
            PRESUPUESTO_DEFINITIVO + 
            COMPROMISOS            + 
            OBLIGACIONES           + 
            PAGOS <> 0
