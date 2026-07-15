MERGE INTO CONSULTAS FIN USING (SELECT '800611_PersonalyCostosPlantaEncargo' INFORME ,TO_CLOB(q'[SELECT '90'                                           CONCEPTO
                 ,'000'                               CONSECUTIVO
                 ,'D00'                               UNIDAD_EJECUTORA_DEPENDENCIA
                 ,'NA'                                DENOMINACIÓN_DEL_CARGO
                 ,'NA'                                GRADO
                 ,SUM(PLAZAS)                         CARGOS_APROBADOS
                 ,SUM(PROVISTOS.CARGOS_PROVISTOS)     CARGOS_PROVISTOS
                 ,'V0'                                TIPO_DE_VINCULACION
                 ,0                                  ASIGNACIÓN_BÁSICA_ANUAL
                 ,0                                   GASTOS_DE_REPRESENTACIÓN_ANUAL
                 ,0                                   PRIMA_TÉCNICA_ANUAL
                 ,0                                   PRIMA_DE_GESTIÓN_ANUAL
                 ,0                                   PRIMA_DE_LOCALIZACIÓN_ANUAL
                 ,0                                   PRIMA_DE_COORDINACIÓN_ANUAL
                 ,0                                   PRIMA_DE_RIESGO_ANUAL
                 ,0                                   PRIMA_EXTRAORDINARIA_ANUAL
                 ,0                                   PRIMA_O_SUBSID_ALIMENTAC_ANUAL
                 ,0                                   AUXILIO_DE_TRANSPORTE_ANUAL
                 ,0                                   PRIMA_DE_ANTIGÜEDAD_ANUAL
                 ,0                                   BONIFICACIÓN_DIRECCIÓN_ANUAL
                 ,0                                   PRIMA_DE_SERVICIOS_ANUAL
                 ,0                                   PRIMA_DE_NAVIDAD_ANUAL
                 ,0                                   BONIFIC_POR_SERVICIOS_ANUAL
                 ,0                                   BONIFIC_DE_RECREACIÓN_ANUAL
                 ,0                                   BONIFIC_GESTIÓN_TER_ANUAL
                 ,0                                   PRIMA_DE_VACACIONES_ANUAL
                 ,0                                   OTRAS_PRIMAS_ANUAL
                 ,0                                   CESANTÍAS_ANUAL
                 ,0                                   INTERESES_CESANTÍAS_ANUAL
FROM CARGOS LEFT JOIN
            (SELECT DISTINCT COMPANIA,ID_DE_CARGO,COUNT(ID_DE_EMPLEADO) CARGOS_PROVISTOS
               FROM PERSONAL
               WHERE PERSONAL.COMPANIA=s$compania$s
               AND ID_DE_EMPLEADO NOT IN(0)
               AND ((PERSONAL.ESTADO_ACTUAL  IN(1) AND TO_CHAR(FECHA_DE_INGRESO,'YYYYMM')<=s$ano$s||s$mesFinal$s)
                             OR (PERSONAL.ESTADO_ACTUAL  IN(3) AND FECHA_DE_RETIRO>=TO_DATE('01/' || s$mesInicial$s || '/' || s$ano$s, 'DD/MM/YYYY')))
               GROUP BY COMPANIA,ID_DE_CARGO)PROVISTOS
     ON  CARGOS.COMPANIA=PROVISTOS.COMPANIA
     AND CARGOS.ID_DE_CARGO=PROVISTOS.ID_DE_CARGO
     WHERE CARGOS.COMPANIA=s$compania$s
AND CARGOS.ESTADOCARGO  IN(1)
UNION A]') || TO_CLOB(q'[LL
SELECT  CONCEPTO
       ,LPAD(ROWNUM,3,'0') CONSECUTIVO
       ,UNIDAD_EJECUTORA_DEPENDENCIA
       ,DENOMINACIÓN_DEL_CARGO
       ,GRADO
       , CARGOS_APROBADOS
       , CARGOS_APROBADOS               CARGOS_PROVISTOS
       ,TIPO_DE_VINCULACION
       ,ASIGNACIÓN_BÁSICA_ANUAL
       ,GASTOS_DE_REPRESENTACIÓN_ANUAL
       ,PRIMA_TÉCNICA_ANUAL
       ,PRIMA_DE_GESTIÓN_ANUAL
       ,PRIMA_DE_LOCALIZACIÓN_ANUAL
       ,PRIMA_DE_COORDINACIÓN_ANUAL
       ,PRIMA_DE_RIESGO_ANUAL
       ,PRIMA_EXTRAORDINARIA_ANUAL
       ,PRIMA_O_SUBSID_ALIMENTAC_ANUAL
       ,AUXILIO_DE_TRANSPORTE_ANUAL
       ,PRIMA_DE_ANTIGÜEDAD_ANUAL
       ,BONIFICACIÓN_DIRECCIÓN_ANUAL
       ,PRIMA_DE_SERVICIOS_ANUAL
       ,PRIMA_DE_NAVIDAD_ANUAL
       ,BONIFIC_POR_SERVICIOS_ANUAL
       ,BONIFIC_DE_RECREACIÓN_ANUAL
       ,BONIFIC_GESTIÓN_TER_ANUAL
       ,PRIMA_DE_VACACIONES_ANUAL
       ,OTRAS_PRIMAS_ANUAL
       ,CESANTÍAS_ANUAL
       ,INTERESES_CESANTÍAS_ANUAL
FROM(
  SELECT     ESCALAFON.CGR_CODIGO                                CONCEPTO
           ,'D07'                                               UNIDAD_EJECUTORA_DEPENDENCIA
           , CARGOS.NOMBRE_DEL_CARGO                             DENOMINACIÓN_DEL_CARGO
           , CARGOS.GRADO                                        GRADO
           , PLAZAS                                             CARGOS_APROBADOS
           , NVL(PROVISTOS.CARGOS_PROVISTOS,0)                                             CARGOS_PROVISTOS
           ,CARGOS.CGR_TIPO_VINCULACION               TIPO_DE_VINCULACION
                 ,0                                  ASIGNACIÓN_BÁSICA_ANUAL
                 ,0                                   GASTOS_DE_REPRESENTACIÓN_ANUAL
                 ,0                                   PRIMA_TÉCNICA_ANUAL
                 ,0                                   PRIMA_DE_GESTIÓN_ANUAL
                 ,0                                   PRIMA_DE_LOCALIZACIÓN_ANUAL
                 ,0                                   PRIMA_DE_COORDINACIÓN_ANUAL
                 ,0                                   PRIMA_DE_RIESGO_ANUAL
                 ,0                                   PRIMA_EXTRAORDINARIA_ANUAL
                 ,0                                   PRIMA_O_SUBSID_ALIMENTAC_ANUAL
                 ,0                                   AUXILIO_DE_TRANSPORTE_ANUAL
                 ,0                                   PRIMA_DE_ANTIGÜEDAD_ANUAL
                 ,0                                   BONIFICACIÓN_DIRECCIÓN_ANUAL
                 ,0                                   PRIMA_DE_SERVICIOS_ANUAL
                 ,0                                   PRIMA_DE_NAVIDAD_ANUAL
                 ,0                                   BONIFIC_POR_SERVICIOS_ANUAL
                 ,0                                   BONIFIC_DE_RECREACIÓN_ANUAL
                 ,0                                   BONIFIC_GESTIÓN_TER_ANUAL
                 ,0             ]') || TO_CLOB(q'[                      PRIMA_DE_VACACIONES_ANUAL
                 ,0                                   OTRAS_PRIMAS_ANUAL
                 ,0                                   CESANTÍAS_ANUAL
                 ,0                                   INTERESES_CESANTÍAS_ANUAL
FROM CARGOS INNER JOIN ESCALAFON
     ON CARGOS.COMPANIA  =ESCALAFON.COMPANIA
     AND CARGOS.ESCALAFON=ESCALAFON.CODIGO
LEFT JOIN
        (SELECT DISTINCT COMPANIA,ID_DE_CARGO,COUNT(ID_DE_EMPLEADO) CARGOS_PROVISTOS
               FROM PERSONAL
               WHERE PERSONAL.COMPANIA=s$compania$s
               AND ID_DE_EMPLEADO NOT IN(0)
              AND ((PERSONAL.ESTADO_ACTUAL  IN(1) AND TO_CHAR(FECHA_DE_INGRESO,'YYYYMM')<=s$ano$s||s$mesFinal$s)
                          OR (PERSONAL.ESTADO_ACTUAL  IN(3) AND FECHA_DE_RETIRO>=TO_DATE('01/' || s$mesInicial$s || '/' || s$ano$s, 'DD/MM/YYYY')))
               GROUP BY COMPANIA,ID_DE_CARGO)PROVISTOS
     ON  CARGOS.COMPANIA=PROVISTOS.COMPANIA
     AND CARGOS.ID_DE_CARGO=PROVISTOS.ID_DE_CARGO
  WHERE CARGOS.COMPANIA=s$compania$s
AND CARGOS.ESTADOCARGO  IN(1)
AND PROVISTOS.COMPANIA IS NULL
  UNION ALL
          SELECT            CARGO_HIST.CODIGO                                CONCEPTO
                 ,  UNIDAD_EJECUTORA_DEPENDENCIA
                 , CARGOS.NOMBRE_DEL_CARGO                           DENOMINACIÓN_DEL_CARGO
                 , CARGOS.GRADO                                        GRADO
                 , CARGOS_APROBADOS
                 , CARGOS_PROVISTOS
                 ,TIPO_DE_VINCULACION
                 ,SUM(ASIGNACIÓN_BÁSICA_ANUAL) ASIGNACIÓN_BÁSICA_ANUAL
                 ,SUM(GASTOS_DE_REPRESENTACIÓN_ANUAL) GASTOS_DE_REPRESENTACIÓN_ANUAL
                 ,SUM(PRIMA_TÉCNICA_ANUAL) PRIMA_TÉCNICA_ANUAL
                 ,SUM(PRIMA_DE_GESTIÓN_ANUAL) PRIMA_DE_GESTIÓN_ANUAL
                 ,SUM(PRIMA_DE_LOCALIZACIÓN_ANUAL) PRIMA_DE_LOCALIZACIÓN_ANUAL
                 ,SUM(PRIMA_DE_COORDINACIÓN_ANUAL) PRIMA_DE_COORDINACIÓN_ANUAL
                 ,SUM(PRIMA_DE_RIESGO_ANUAL) PRIMA_DE_RIESGO_ANUAL
                 ,SUM(PRIMA_EXTRAORDINARIA_ANUAL) PRIMA_EXTRAORDINARIA_ANUAL
                 ,SUM(PRIMA_O_SUBSID_ALIMENTAC_ANUAL) PRIMA_O_SUBSID_ALIMENTAC_ANUAL
                 ,SUM(AUXILIO_DE_TRANSPORTE_ANUAL) AUXILIO_DE_TRANSPORTE_ANUAL
                 ,SUM(PRIMA_DE_ANTIGÜEDAD_ANUAL) PRIMA_DE_ANTIGÜEDAD_ANUAL
                 ,SUM(BONIFICACIÓN_DIRECCIÓN_ANUAL) BONIFICACIÓN_DIRECCIÓN_ANUAL
                 ,SUM(PRIMA_DE_SERVICIOS_ANUAL) PRIMA_DE_SERVICIOS_ANUAL
                 ,SUM(PRIMA_DE_NAVIDAD_ANUAL)  PRIMA_DE_NAVIDAD_ANUAL
                 ,SUM(BONIFIC_POR_SERVICIOS_ANUAL) BONIFIC__SERVICIOS_ANUAL
                 ,SUM(BONIFIC_DE_RECREACIÓN_ANUAL)  BONIFIC_DE_RECREACIÓN_ANUAL
                 ,SUM(BONIFIC_GESTIÓN_TER_ANUAL)  BONIFIC_GESTIÓN_TER_ANUAL
                 ,SUM(PRIMA_DE_VACACIONES_ANUAL) PRIMA_DE_VACACIONES_ANUAL
                 ,SUM(OTRAS_PRIMAS_ANUAL) OTRAS_]') || TO_CLOB(q'[PRIMAS_ANUAL
                 ,SUM(CESANTÍAS_ANUAL) CESANTÍAS_ANUAL
                 ,SUM(INTERESES_CESANTÍAS_ANUAL) INTERESES_CESANTÍAS_ANUAL
FROM CARGOS INNER JOIN
  (SELECT           PERSONAL.ID_DE_EMPLEADO, HISTORICOS.COMPANIA,HISTORICOS.ANO, FORMANOMBRAMIENTO.CGR_TIPO_VINCULACION TIPO_DE_VINCULACION,ESTABLECIMIENTOS_DOCENTES.CGR_DEPENDENCIA  UNIDAD_EJECUTORA_DEPENDENCIA,ESCALAFON.CGR_CODIGO CODIGO   ,
          CASE WHEN PERSONAL_HISTORICO.CARGO_ENCARGO IS NULL THEN PERSONAL_HISTORICO.ID_DE_CARGO ELSE PERSONAL_HISTORICO.CARGO_ENCARGO END ID_DE_CARGO,
          CASE WHEN PERSONAL_HISTORICO.CARGO_ENCARGO IS NULL THEN PERSONAL_HISTORICO.NOMBRE_DE_CARGO ELSE PERSONAL_HISTORICO.NOMBRE_DEL_CARGO_ENCARGO END NOMBRE_DEL_CARGO,
           SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('01')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                            ASIGNACIÓN_BÁSICA_ANUAL
                 , SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('02')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                             GASTOS_DE_REPRESENTACIÓN_ANUAL
                 , SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('03')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                             PRIMA_TÉCNICA_ANUAL
                 , SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('04')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                              PRIMA_DE_GESTIÓN_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('05')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                              PRIMA_DE_LOCALIZACIÓN_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('06')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                              PRIMA_DE_COORDINACIÓN_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('07')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                              PRIMA_DE_RIESGO_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('08')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                              PRIMA_EXTRAORDINARIA_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('09')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                               PRIMA_O_SUBSID_ALIMENTAC_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('10')
                     THEN HISTORICOS.VALOR
                     EL]') || TO_CLOB(q'[SE 0
                     END)                                               AUXILIO_DE_TRANSPORTE_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('11')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                PRIMA_DE_ANTIGÜEDAD_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('12')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                BONIFICACIÓN_DIRECCIÓN_ANUAL
                  ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('13')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 PRIMA_DE_SERVICIOS_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('14')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 PRIMA_DE_NAVIDAD_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('15')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 BONIFIC_POR_SERVICIOS_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('16')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 BONIFIC_DE_RECREACIÓN_ANUAL
              ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('17')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 BONIFIC_GESTIÓN_TER_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('18')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                 PRIMA_DE_VACACIONES_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('19')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                OTRAS_PRIMAS_ANUAL
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('20')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)                                                CESANTÍAS_ANUAL
              ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('21')
                     THEN HISTORICOS.VALOR
                     ELSE 0
                     END)       INTERESES_CESANTÍAS_ANUAL
    FROM HISTORICOS   INNER JOIN  PERSONAL
               ON PERSONAL.COMPANIA           =HISTORICOS.COMPANIA
               AND PERSONAL.ID_DE_EMPLEADO    =HISTORICOS.ID_DE_EMPLEADO
        INNER JOIN PERSONAL_HISTORICO
         ON  HISTORICOS.COMPANIA=PERSONAL_HISTORICO.COMPANIA
                   AND HISTORIC]') || TO_CLOB(q'[OS.ID_DE_PROCESO=PERSONAL_HISTORICO.ID_DE_PROCESO
                   AND HISTORICOS.ANO=PERSONAL_HISTORICO.ANO
                   AND HISTORICOS.MES=PERSONAL_HISTORICO.MES
                   AND HISTORICOS.PERIODO=PERSONAL_HISTORICO.PERIODO
                   AND PERSONAL.ID_DE_EMPLEADO=PERSONAL_HISTORICO.ID_DE_EMPLEADO
             INNER JOIN CONCEPTOS
              ON HISTORICOS.COMPANIA      =CONCEPTOS.COMPANIA
               AND HISTORICOS.ID_DE_CONCEPTO  =CONCEPTOS.ID_DE_CONCEPTO
             INNER JOIN PERIODOS
                   ON  HISTORICOS.COMPANIA=PERIODOS.COMPANIA
                   AND HISTORICOS.ID_DE_PROCESO=PERIODOS.ID_DE_PROCESO
                   AND HISTORICOS.ANO=PERIODOS.ANO
                   AND HISTORICOS.MES=PERIODOS.MES
                   AND HISTORICOS.PERIODO=PERIODOS.PERIODO
                   INNER JOIN FORMANOMBRAMIENTO
          ON  PERSONAL_HISTORICO.COMPANIA   =FORMANOMBRAMIENTO.COMPANIA
          AND PERSONAL_HISTORICO.DE_CARRERA  =FORMANOMBRAMIENTO.IDFORMA
         INNER  JOIN ESCALAFON
               ON PERSONAL_HISTORICO.COMPANIA=ESCALAFON.COMPANIA
               AND PERSONAL_HISTORICO.ESCALAFON=ESCALAFON.CODIGO
          inner JOIN ESTABLECIMIENTOS_DOCENTES
          ON  PERSONAL_HISTORICO.COMPANIA   =ESTABLECIMIENTOS_DOCENTES.COMPANIA
          AND PERSONAL_HISTORICO.CODIGO_ESTABLECIMIENTO  =ESTABLECIMIENTOS_DOCENTES.CODIGO
            WHERE HISTORICOS.COMPANIA= s$compania$s
AND HISTORICOS.ANO=s$ano$s
  AND HISTORICOS.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
AND  (CONCEPTOS.CLASE IN (3) OR CONCEPTOS.ID_DE_CONCEPTO = 483)
  AND PERIODOS.ACUMULADO  NOT IN (0)
   AND PERSONAL.ID_DE_EMPLEADO NOT IN(0)
  GROUP BY    PERSONAL.ID_DE_EMPLEADO, HISTORICOS.COMPANIA,HISTORICOS.ANO,  FORMANOMBRAMIENTO.CGR_TIPO_VINCULACION,ESTABLECIMIENTOS_DOCENTES.CGR_DEPENDENCIA,ESCALAFON.CGR_CODIGO,
          CASE WHEN PERSONAL_HISTORICO.CARGO_ENCARGO IS NULL THEN PERSONAL_HISTORICO.ID_DE_CARGO ELSE PERSONAL_HISTORICO.CARGO_ENCARGO END ,
          CASE WHEN PERSONAL_HISTORICO.CARGO_ENCARGO IS NULL THEN PERSONAL_HISTORICO.NOMBRE_DE_CARGO ELSE PERSONAL_HISTORICO.NOMBRE_DEL_CARGO_ENCARGO END
     ) CARGO_HIST
 ON CARGOS.COMPANIA= CARGO_HIST.COMPANIA
 AND CARGOS.ID_DE_CARGO=CARGO_HIST.ID_DE_CARGO
 LEFT JOIN ( SELECT   CARGOS. COMPANIA
                   ,CARGOS.ID_DE_CARGO
                   ,CARGOS.CGR_TIPO_VINCULACION
                   ,CARGOS.ESCALAFON
                   ,SUM(PLAZAS)                             CARGOS_APROBADOS
                   ,SUM(PROVISTOS.CARGOS_PROVISTOS)         CARGOS_PROVISTOS
                    FROM CARGOS LEFT JOIN
                      (SELECT  PERSONAL.COMPANIA,ID_DE_CARGO,CGR_TIPO_VINCULACION,ESCALAFON,COUNT(ID_DE_EMPLEADO) CARGOS_PROVISTOS
                         FROM PERSONAL INNER JOIN FORMANOMBRAMIENTO
                             ON  PERSONAL.COMPANIA=FORMANOMBRAMIENTO.COMPANIA
                             AND PERSONAL.DE_CARRERA=FORMANOMBRAMIENTO]') || TO_CLOB(q'[.IDFORMA
                         WHERE PERSONAL.COMPANIA=s$compania$s
                         AND ID_DE_EMPLEADO NOT IN(0)
                         AND ((PERSONAL.ESTADO_ACTUAL  IN(1) AND TO_CHAR(FECHA_DE_INGRESO,'YYYYMM')<=s$ano$s||s$mesFinal$s)
                             OR (PERSONAL.ESTADO_ACTUAL  IN(3) AND FECHA_DE_RETIRO>=TO_DATE('01/' || s$mesInicial$s || '/' || s$ano$s, 'DD/MM/YYYY')))
                             GROUP BY PERSONAL.COMPANIA,ID_DE_CARGO,ESCALAFON,CGR_TIPO_VINCULACION)PROVISTOS
                                 ON  CARGOS.COMPANIA=PROVISTOS.COMPANIA
                                 AND CARGOS.ID_DE_CARGO=PROVISTOS.ID_DE_CARGO
                                 AND CARGOS.ESCALAFON=PROVISTOS.ESCALAFON
                                 AND CARGOS.CGR_TIPO_VINCULACION=PROVISTOS.CGR_TIPO_VINCULACION
                              WHERE CARGOS.COMPANIA=s$compania$s
                            AND CARGOS.ESTADOCARGO IN(1)
                            GROUP  BY CARGOS.COMPANIA,CARGOS.ID_DE_CARGO
                            ,CARGOS.CGR_TIPO_VINCULACION ,CARGOS.ESCALAFON) CARGOS_INFORME
                  ON CARGOS.COMPANIA                   = CARGOS_INFORME.COMPANIA
                   AND CARGOS.ID_DE_CARGO              = CARGOS_INFORME.ID_DE_CARGO
                   AND CARGOS.CGR_TIPO_VINCULACION     = CARGOS_INFORME.CGR_TIPO_VINCULACION
                   AND CARGOS.ESCALAFON                = CARGOS_INFORME.ESCALAFON
WHERE CARGOS.COMPANIA= s$compania$s
AND CARGO_HIST.ANO=s$ano$s
  AND CARGOS.ESTADOCARGO  IN(1)
  AND CARGO_HIST.ID_DE_EMPLEADO NOT IN(0)
 GROUP BY CARGO_HIST.CODIGO
                 ,  UNIDAD_EJECUTORA_DEPENDENCIA
                 , CARGOS.NOMBRE_DEL_CARGO
                 , CARGOS.GRADO
                 , CARGOS_APROBADOS
                 , CARGOS_PROVISTOS
                 ,TIPO_DE_VINCULACION
ORDER BY CONCEPTO,GRADO
)INFORME]') CONSULTA, 6 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);