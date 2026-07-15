MERGE INTO CONSULTAS FIN USING (SELECT '002068BalanceGeneralMEParalelo' INFORME ,TO_CLOB(q'[SELECT COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE_1
      , SUM(SANTERIOR) SANTERIOR
      , SUM(NUEVO) NUEVO
      , CLASE
       , CLASEORDEN
       , TIPOCUENTAC
      ,  CORRIENTE
FROM (
SELECT 
       COMPANIA, 
       CODIGO , 
       CENTRO_COSTO , 
       TERCERO , 
       AUXILIAR , 
       REFERENCIA  , 
       FUENTE_RECURSOS , 
       NOMBRE_1  , 
    SALDOs$mestrabajo$s SANTERIOR,
    0 NUEVO,
    CASE
        WHEN SUBSTR(CODIGO,1,1) = 1
        THEN 'ACTIVO'
        ELSE
        CASE 
            WHEN SUBSTR(CODIGO,1,1) = 2
            THEN 'PASIVO'
            ELSE
            CASE
                WHEN SUBSTR(CODIGO,1,1) = 3
                THEN'PATRIMONIO'
                ELSE
                CASE
                    WHEN SUBSTR(CODIGO,1,1) = 4
                    THEN 'INGRESOS'
                    ELSE
                    CASE 
                        WHEN SUBSTR(CODIGO,1,1) = 5
                        THEN 'GASTOS'
                        ELSE
                        CASE
                            WHEN SUBSTR(CODIGO,1,1) = 6
                            THEN 'COSTOS DE VENTAS'
                            ELSE
                            CASE
                                WHEN SUBSTR(CODIGO,1,1) = 7
                                THEN 'COSTOS DE PRODUCCI¿N'
                                ELSE
                                CASE
                                    WHEN SUBSTR(CODIGO,1,1) = 8 
                                    THEN 'CUENTAS DE ORDEN DEUDORAS'
                                    ELSE
                                    CASE
                                        WHEN SUBSTR(CODIGO,1,1) = 9
                                        THEN 'CUENTAS DE ORDEN ACREEDORAS'
                                    END
                                END
                            END
                        END
                    END
                END
            END
        END
    END AS CLASE,
     CASE
        WHEN CLASE='0'
        THEN 'z' || CODIGO
        ELSE CODIGO 
    END AS ORDEN,
    SUBSTR(ORDEN,1) AS Claseorden,
    CASE
        WHEN CORRIENTE IS NULL 
        OR CORRIENTE = 0
        THEN 'NO CORRIENTE'
        ELSE 'CORRIENTE'
    END AS TIPOCUENTAC,
    CORRIENTE
FROM (s$baseBalance$s) 
BASE
UNION ALL
SELECT
    COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE_1
      , 0 NUEVO
    ,SALDOs$mesComparar$s SANTERIOR,
    CASE
        WHEN SUBSTR(CODIGO,1,1) = 1
        THEN 'ACTIVO'
        ELSE
        CASE 
            WHEN SUBSTR(CODIGO,1,1) = 2
            THEN 'PASIVO'
            ELSE
            CASE
                WHEN SUBSTR(CODIGO,1,1) = 3
                THEN'PATRIMONIO'
                ELSE
               ]') || TO_CLOB(q'[ CASE
                    WHEN SUBSTR(CODIGO,1,1) = 4
                    THEN 'INGRESOS'
                    ELSE
                    CASE 
                        WHEN SUBSTR(CODIGO,1,1) = 5
                        THEN 'GASTOS'
                        ELSE
                        CASE
                            WHEN SUBSTR(CODIGO,1,1) = 6
                            THEN 'COSTOS DE VENTAS'
                            ELSE
                            CASE
                                WHEN SUBSTR(CODIGO,1,1) = 7
                                THEN 'COSTOS DE PRODUCCI¿N'
                                ELSE
                                CASE
                                    WHEN SUBSTR(CODIGO,1,1) = 8 
                                    THEN 'CUENTAS DE ORDEN DEUDORAS'
                                    ELSE
                                    CASE
                                        WHEN SUBSTR(CODIGO,1,1) = 9
                                        THEN 'CUENTAS DE ORDEN ACREEDORAS'
                                    END
                                END
                            END
                        END
                    END
                END
            END
        END
    END AS CLASE,
     CASE
        WHEN CLASE='0'
        THEN 'z' || CODIGO
        ELSE CODIGO 
    END AS ORDEN,
    SUBSTR(ORDEN,1) AS Claseorden,
    CASE
        WHEN CORRIENTE IS NULL 
        OR CORRIENTE = 0
        THEN 'NO CORRIENTE'
        ELSE 'CORRIENTE'
    END AS TIPOCUENTAC,
    CORRIENTE 
FROM (s$baseBalanceUnion$s))
WHERE LENGTH(CODIGO)=s$digitos$s
 s$saldoCero$s
GROUP BY COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE_1
      , CLASE,
      ORDEN,
      CLASEORDEN,
      TIPOCUENTAC,
      CORRIENTE
order by COMPANIA,CLASE,TIPOCUENTAC,CLASEORDEN,CORRIENTE]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, 'jcortes' CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);