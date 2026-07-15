MERGE INTO CONSULTAS FIN USING (SELECT '002433ReporteNovedadesProrroga' INFORME ,TO_CLOB(q'[SELECT
     COMPANIA,
     NUMERO NUMERO_RADICADO,
     PROCESOS,
     PROCESO_NOM PROCESO,
     FECHA_RADICADO,
     DEPENDENCIA_NOM DEPENDENCIA,   
     FECHA_LIMITE_PROCESO FECHA_LIMITE,
     VALOR_DTV2 AS NOVEDAD,
     CASE WHEN PRORROGA NOT IN (0) THEN 'Si' ELSE 'No' END PRORROGA,
     FECHA_PRORROGA,
     CASE WHEN PRORROGA NOT IN (0) THEN PCK_SYSMAN_UTL.FC_DIASHABIL(COMPANIA, NVL(FECHA_PRORROGA,SYSDATE), SYSDATE, 0) ELSE  DIAS_PRORROGA END DIAS_PRORROGA,
     USUARIO_PRORROGA,
     USUARIO_INTERNO USUARIO_RETIRA_PRORROGA,
     
     FECHA_SALIDA FECHA_RETIRA_PRORROGA
 FROM
     (
         SELECT
             T.COMPANIA,
             T.PROCESOS,
             TT.NOMBRE      NOM_TIPO_TRAMITE,
             T.TIPO_TRAMITE,
             T.NUMERO,
             P.NOMBRE       PROCESO_NOM,
             T.DEPENDENCIA DEPENDENCIAS,
             D.NOMBRE       DEPENDENCIA_NOM,
             TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY') FECHA_RADICADO,
 
             TO_CHAR(T.FECHA, 'DD/MM/YYYY') FECHA_PASE,
             TO_CHAR(PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA => T.COMPANIA, UN_FECHA => TO_DATE(TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY'
             ), 'DD/MM/YYYY'), UN_DIAS =>
                 (CASE
                     WHEN SUM(N_DIAS.TIEMPO_ESTIMADO) = 0 THEN
                         1
                     ELSE
                         SUM(N_DIAS.TIEMPO_ESTIMADO)
                 END) + T.DIAS_PRORROGA
             ), 'DD/MM/YYYY') FECHA_LIMITE_PROCESO,
 
             N.PRORROGA,
             T.FECHA_PRORROGA,
             T.DIAS_PRORROGA, 
             T.USUARIO_INTERNO USUARIO_PRORROGA,
             null USUARIO_INTERNO,
             null AS FECHA_SALIDA,
             DTV2.CODIGO_NODO_VARIABLE VARIABLE_DTV2,
CASE DTV2.TIPO_DATO WHEN 8 THEN DTV2.VALOR_TEXTO
WHEN 6 THEN TO_CHAR(DTV2.VALOR)
WHEN 7 THEN TO_CHAR(DTV2.VALOR_FECHA, 'DD/MM/YYYY')END VALOR_DTV2
         FROM
             TRAMITES T
             
              INNER JOIN D_TRAMITES DT
              ON DT.COMPANIA = T.COMPANIA
             AND DT.PROCESOS = T.PROCESOS
             AND DT.TIPO_TRAMITE = T.TIPO_TRAMITE
             AND DT.NUMERO = T.NUMERO
            LEFT JOIN D_TRAMITE_VARIABLES DTV2
             ON T.COMPANIA = DTV2.COMPANIA
            AND T.PROCESOS = DTV2.CODIGO_PROCESO
            AND T.TIPO_TRAMITE = DTV2.TIPO_TRAMITE
            AND T.NUMERO = DTV2.NUMERO_TRAMITE
            AND T.NODO_ACTUAL = DTV2.CODIGO_NODO
         
             INNER JOIN PROCESOS                                                                                                                                                                                                                   P ON P.COMPANIA = T.COMPANIA
             AND P.CODIGO = T.PROCESOS
             LEFT JOIN PROCEDENCIA_TRAMITE                                                                                                                                                                                                        PT ON T.COMPANIA = PT]') || TO_CLOB(q'[.COMPANIA
                                                 AND T.PROCEDENCIA = PT.CODIGO
             INNER JOIN NODOS                                                                                                                                                                                                                      N ON T.COMPANIA = N.COMPANIA
                                   AND T.PROCESOS = N.CODIGO_PROCESO
                                   AND T.NODO_ACTUAL = N.CODIGO
    
             LEFT JOIN USUARIO                                                                                                                                                                                                                    U ON U.CODIGO = T.USUARIO_INTERNO
             LEFT JOIN DEPENDENCIA                                                                                                                                                                                                                D ON T.COMPANIA = D.COMPANIA
                                        AND T.DEPENDENCIA = D.CODIGO
             INNER JOIN PROYECCIONES_TRAMITE                                                                                                                                                                                                       PT1 ON T.COMPANIA = PT1.COMPANIA
                                                    AND T.TIPO_TRAMITE = PT1.TIPO_TRAMITE
                                                    AND T.PROCESOS = PT1.PROCESO
                                                    AND T.NUMERO = PT1.TRAMITE
                                                    AND T.NODO_ACTUAL = PT1.NODO
             INNER JOIN TIPOTRAMITES                                                                                                                                                                                                               TT ON T.COMPANIA = TT.COMPANIA
                                           AND T.PROCESOS = TT.PROCESOS
                                           AND T.TIPO_TRAMITE = TT.TIPOTRAMITE
             INNER JOIN (
                 SELECT
                     NODOS.COMPANIA,
                     NODOS.CODIGO_PROCESO,
                     SUM(TIEMPO_ESTIMADO) AS TIEMPO_ESTIMADO
                 FROM
                     NODOS
                     INNER JOIN PROCESOS ON NODOS.COMPANIA = PROCESOS.COMPANIA
                                            AND NODOS.CODIGO_PROCESO = PROCESOS.CODIGO
                 GROUP BY
                     NODOS.COMPANIA,
                     CODIGO_PROCESO
             ) N_DIAS ON T.COMPANIA = N_DIAS.COMPANIA
                         AND T.PROCESOS = N_DIAS.CODIGO_PROCESO
         WHERE N.PRORROGA NOT IN (0)
             AND T.ESTADO = 4 group by T.COMPANIA, T.PROCESOS, TT.NOMBRE, T.TIPO_TRAMITE, T.NUMERO, 
P]') || TO_CLOB(q'[.NOMBRE, T.DEPENDENCIA, D.NOMBRE, TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY'), TO_CHAR(T.FECHA, 'DD/MM/YYYY'), 
N.PRORROGA, T.FECHA_PRORROGA, T.DIAS_PRORROGA, T.USUARIO_INTERNO, null, 
null, DTV2.CODIGO_NODO_VARIABLE, CASE DTV2.TIPO_DATO WHEN 8 THEN DTV2.VALOR_TEXTO WHEN 6 THEN TO_CHAR(DTV2.VALOR) WHEN 7 THEN TO_CHAR(DTV2.VALOR_FECHA, 'DD/MM/YYYY')END
  UNION ALL
      SELECT
             T.COMPANIA,
             T.PROCESOS,
             TT.NOMBRE      NOM_TIPO_TRAMITE,
             T.TIPO_TRAMITE,
             T.NUMERO,
             P.NOMBRE       PROCESO_NOM,
             T.DEPENDENCIA DEPENDENCIAS,
             D.NOMBRE       DEPENDENCIA_NOM,
             TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY') FECHA_RADICADO,
 
             TO_CHAR(T.FECHA, 'DD/MM/YYYY') FECHA_PASE,
             TO_CHAR(PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA => T.COMPANIA, UN_FECHA => TO_DATE(TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY'
             ), 'DD/MM/YYYY'), UN_DIAS =>
                 (CASE
                     WHEN SUM(N_DIAS.TIEMPO_ESTIMADO) = 0 THEN
                         1
                     ELSE
                         SUM(N_DIAS.TIEMPO_ESTIMADO)
                 END) + T.DIAS_PRORROGA
             ), 'DD/MM/YYYY') FECHA_LIMITE_PROCESO,
 
             N.PRORROGA,
             T.FECHA_PRORROGA,
             T.DIAS_PRORROGA,
             NU.USUARIO_INTERNO USUARIO_PRORROGA,
             DT.USUARIO_INTERNO,
             TO_CHAR(DT.FECHA,'DD/MM/YYYY') AS FECHA_SALIDA,
             DTV2.CODIGO_NODO_VARIABLE VARIABLE_DTV2,
CASE DTV2.TIPO_DATO WHEN 8 THEN DTV2.VALOR_TEXTO
WHEN 6 THEN TO_CHAR(DTV2.VALOR)
WHEN 7 THEN TO_CHAR(DTV2.VALOR_FECHA, 'DD/MM/YYYY')END VALOR_DTV2
         FROM
             TRAMITES T
             INNER JOIN D_TRAMITES DT
              ON DT.COMPANIA = T.COMPANIA
             AND DT.PROCESOS = T.PROCESOS
             AND DT.TIPO_TRAMITE = T.TIPO_TRAMITE
             AND DT.NUMERO = T.NUMERO
              INNER JOIN NODOS NP
         ON NP.COMPANIA = DT.COMPANIA
         AND NP.CODIGO_PROCESO = DT.PROCESOS
         AND NP.CODIGO = DT.NODO_ORIGEN
         
         LEFT JOIN D_TRAMITE_VARIABLES DTV2
 ON DT.COMPANIA = DTV2.COMPANIA
AND DT.PROCESOS = DTV2.CODIGO_PROCESO
AND DT.TIPO_TRAMITE = DTV2.TIPO_TRAMITE
AND DT.NUMERO = DTV2.NUMERO_TRAMITE
AND DT.NODO_ORIGEN = DTV2.CODIGO_NODO
 INNER JOIN (
 SELECT DT.COMPANIA, 
DT.PROCESOS, 
DT.TIPO_TRAMITE, 
DT.NUMERO, 
DT.USUARIO_INTERNO
FROM D_TRAMITES DT
INNER JOIN NODOS NPU
  ON NPU.COMPANIA = DT.COMPANIA
AND NPU.CODIGO_PROCESO = DT.PROCESOS
AND NPU.CODIGO = DT.NODO_DESTINO
AND NPU.PRORROGA NOT IN (0)
 ) NU
 ON NU.COMPANIA = T.COMPANIA
AND NU.PROCESOS = T.PROCESOS
AND NU.TIPO_TRAMITE = T.TIPO_TRAMITE
AND NU.NUMERO = T.NUMERO
         
             INNER JOIN PROCESOS                                                                                                                                                                                       ]') || TO_CLOB(q'[                            P ON P.COMPANIA = T.COMPANIA
                                      AND P.CODIGO = T.PROCESOS
             LEFT JOIN PROCEDENCIA_TRAMITE                                                                                                                                                                                                        PT ON T.COMPANIA = PT.COMPANIA
                                                 AND T.PROCEDENCIA = PT.CODIGO
             INNER JOIN NODOS                                                                                                                                                                                                                      N ON T.COMPANIA = N.COMPANIA
                                   AND T.PROCESOS = N.CODIGO_PROCESO
                                   AND T.NODO_ACTUAL = N.CODIGO
            
             LEFT JOIN USUARIO                                                                                                                                                                                                                    U ON U.CODIGO = T.USUARIO_INTERNO
             LEFT JOIN DEPENDENCIA                                                                                                                                                                                                                D ON T.COMPANIA = D.COMPANIA
                                        AND T.DEPENDENCIA = D.CODIGO
             INNER JOIN PROYECCIONES_TRAMITE                                                                                                                                                                                                       PT1 ON T.COMPANIA = PT1.COMPANIA
                                                    AND T.TIPO_TRAMITE = PT1.TIPO_TRAMITE
                                                    AND T.PROCESOS = PT1.PROCESO
                                                    AND T.NUMERO = PT1.TRAMITE
                                                    AND T.NODO_ACTUAL = PT1.NODO
             INNER JOIN TIPOTRAMITES                                                                                                                                                                                                               TT ON T.COMPANIA = TT.COMPANIA
                                           AND T.PROCESOS = TT.PROCESOS
                                           AND T.TIPO_TRAMITE = TT.TIPOTRAMITE
             INNER JOIN (
                 SELECT
                     NODOS.COMPANIA,
                     NODOS.CODIGO_PROCESO,
                     SUM(TIEMPO_ESTIMADO) AS TIEMPO_ESTIMADO
                 FROM
                     NODOS
                     INNER JOIN PROCESOS ON NODOS.COMPANIA = PROCESOS.COMPANIA
                                            AND NODOS.CODIGO_PROCESO = PROCESOS.CODIGO
                 GROUP BY
]') || TO_CLOB(q'[                     NODOS.COMPANIA,
                     CODIGO_PROCESO
             ) N_DIAS ON T.COMPANIA = N_DIAS.COMPANIA
                         AND T.PROCESOS = N_DIAS.CODIGO_PROCESO
         WHERE NP.PRORROGA NOT IN (0)
             AND T.ESTADO = 4 group by T.COMPANIA, T.PROCESOS, TT.NOMBRE, T.TIPO_TRAMITE, T.NUMERO, 
P.NOMBRE, T.DEPENDENCIA, D.NOMBRE, TO_CHAR(T.FECHA_REAL, 'DD/MM/YYYY'), TO_CHAR(T.FECHA, 'DD/MM/YYYY'), 
N.PRORROGA, T.FECHA_PRORROGA, T.DIAS_PRORROGA, NU.USUARIO_INTERNO, DT.USUARIO_INTERNO, 
TO_CHAR(DT.FECHA,'DD/MM/YYYY'), DTV2.CODIGO_NODO_VARIABLE, CASE DTV2.TIPO_DATO WHEN 8 THEN DTV2.VALOR_TEXTO WHEN 6 THEN TO_CHAR(DTV2.VALOR) WHEN 7 THEN TO_CHAR(DTV2.VALOR_FECHA, 'DD/MM/YYYY')END 
     ) CONS     
    WHERE COMPANIA = s$compania$s
            AND PROCESOS BETWEEN 's$procesoIni$s' AND  's$procesoFin$s'
            AND DEPENDENCIAS BETWEEN 's$dependenciaIni$s' AND  's$dependenciaFin$s'
            AND FECHA_RADICADO BETWEEN 's$fechaInicial$s' AND  's$fechaFinal$s'
    ORDER BY NUMERO ASC
 ]') CONSULTA, 35 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);