SELECT '90'                                           CONCEPTO
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
               AND ((PERSONAL.ESTADO_ACTUAL  IN(1) AND TO_NUMBER(TO_CHAR(FECHA_DE_INGRESO,'YYYYMM'))<=s$ano$s||s$mesFinal$s)
                   OR (PERSONAL.ESTADO_ACTUAL  IN(3) AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'YYYYMM'))>s$ano$s||s$mesFinal$s))
               GROUP BY COMPANIA,ID_DE_CARGO)PROVISTOS
     ON  CARGOS.COMPANIA=PROVISTOS.COMPANIA
     AND CARGOS.ID_DE_CARGO=PROVISTOS.ID_DE_CARGO 
     WHERE CARGOS.COMPANIA=s$compania$s
AND CARGOS.ESTADOCARGO  IN(1)

UNION ALL

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
                 ,0                                   PRIMA_DE_VACACIONES_ANUAL
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
               AND ((PERSONAL.ESTADO_ACTUAL  IN(1) AND TO_NUMBER(TO_CHAR(FECHA_DE_INGRESO,'YYYYMM'))<=s$ano$s||s$mesFinal$s)
                 OR (PERSONAL.ESTADO_ACTUAL  IN(3) AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'YYYYMM'))>s$ano$s||s$mesFinal$s))
               GROUP BY COMPANIA,ID_DE_CARGO)PROVISTOS
     ON  CARGOS.COMPANIA=PROVISTOS.COMPANIA
     AND CARGOS.ID_DE_CARGO=PROVISTOS.ID_DE_CARGO 
  WHERE CARGOS.COMPANIA=s$compania$s
AND CARGOS.ESTADOCARGO  IN(1)
AND PROVISTOS.COMPANIA IS NULL
 
  UNION ALL
          SELECT   ESCALAFON.CGR_CODIGO                                CONCEPTO
                 , ESTABLECIMIENTOS_DOCENTES.CGR_DEPENDENCIA           UNIDAD_EJECUTORA_DEPENDENCIA
                 , CARGOS.NOMBRE_DEL_CARGO                  DENOMINACIÓN_DEL_CARGO
                 , CARGOS.GRADO                                        GRADO
                 , CARGOS_APROBADOS
                 , CARGOS_PROVISTOS
                 ,FORMANOMBRAMIENTO.CGR_TIPO_VINCULACION               TIPO_DE_VINCULACION
                 ,SUM(CASE WHEN CONCEPTOS.CGR_CONCEPTO  IN('01')
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
                     ELSE 0
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
          FROM CARGOS LEFT JOIN PERSONAL
                  ON    CARGOS.COMPANIA=PERSONAL.COMPANIA 
                   AND  CARGOS.ESCALAFON=PERSONAL.ESCALAFON 
                   AND  CARGOS.ID_DE_CARGO=PERSONAL.ID_DE_CARGO
             INNER JOIN  HISTORICOS 
               ON PERSONAL.COMPANIA           =HISTORICOS.COMPANIA
               AND PERSONAL.ID_DE_EMPLEADO    =HISTORICOS.ID_DE_EMPLEADO
             INNER JOIN CONCEPTOS
              ON HISTORICOS.COMPANIA      =CONCEPTOS.COMPANIA
               AND HISTORICOS.ID_DE_CONCEPTO  =CONCEPTOS.ID_DE_CONCEPTO   
             INNER JOIN PERIODOS
                   ON  HISTORICOS.COMPANIA=PERIODOS.COMPANIA
                   AND HISTORICOS.ID_DE_PROCESO=PERIODOS.ID_DE_PROCESO
                   AND HISTORICOS.ANO=PERIODOS.ANO
                   AND HISTORICOS.MES=PERIODOS.MES
                   AND HISTORICOS.PERIODO=PERIODOS.PERIODO
           INNER  JOIN ESCALAFON
               ON PERSONAL.COMPANIA=ESCALAFON.COMPANIA
               AND PERSONAL.ESCALAFON=ESCALAFON.CODIGO
            INNER JOIN FORMANOMBRAMIENTO
                  ON  PERSONAL.COMPANIA   =FORMANOMBRAMIENTO.COMPANIA
                  AND PERSONAL.DE_CARRERA  =FORMANOMBRAMIENTO.IDFORMA
              LEFT JOIN ESTABLECIMIENTOS_DOCENTES
                  ON  PERSONAL.COMPANIA   =ESTABLECIMIENTOS_DOCENTES.COMPANIA
                  AND PERSONAL.CODIGO_ESTABLECIMIENTO  =ESTABLECIMIENTOS_DOCENTES.CODIGO
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
                             AND PERSONAL.DE_CARRERA=FORMANOMBRAMIENTO.IDFORMA
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
AND HISTORICOS.ANO=s$ano$s
  AND HISTORICOS.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
AND  CONCEPTOS.CLASE IN (3)
  AND PERIODOS.ACUMULADO  NOT IN (0)
  AND CARGOS.ESTADOCARGO  IN(1)
  AND PERSONAL.ID_DE_EMPLEADO NOT IN(0)
GROUP BY ESCALAFON.CGR_CODIGO 
       , ESTABLECIMIENTOS_DOCENTES.CGR_DEPENDENCIA  
       , CARGOS.NOMBRE_DEL_CARGO 
       , CARGOS.GRADO
       ,FORMANOMBRAMIENTO.CGR_TIPO_VINCULACION
       , CARGOS_APROBADOS
       , CARGOS_PROVISTOS
)INFORME