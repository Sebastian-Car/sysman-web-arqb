SELECT  PERSONAL_HISTORICO.CODIGO_ESTABLECIMIENTO  ESTABLECIMIENTO,
        PERSONAL_HISTORICO.NOMBRE_ESTABLECIMIENTO  NOMBREESTABLECIMIENTO, 
        PERSONAL_HISTORICO.ID_DE_EMPLEADO ID_DE_EMPLEADO,
        PERSONAL_HISTORICO.ID_CENTRO_DE_COSTO ID_CENTRO_DE_COSTO, 
        PERSONAL_HISTORICO.DEPENDENCIA DEPENDENCIA, 
        PERSONAL_HISTORICO.NOMBRE_DEPENDENCIA  NOMBRE, 
        PERSONAL_HISTORICO.NOMBRECOMPLETO  NOMCOMPLETO, 
        PERSONAL_HISTORICO.NUMERO_DCTO NUMERO_DCTO, 
        PERSONAL_HISTORICO.FECHA_DE_INGRESO FECHA_DE_INGRESO, 
        PERSONAL_HISTORICO.FECHATERCONTRATO  FECHATERCONTRATO, 
        PERSONAL_HISTORICO.NOMBRE_CENTRO_DE_COSTO NOMBRE_CENTRO_DE_COSTO,
        PERSONAL_HISTORICO.NOMBRE_DE_CARGO NOMBRE_DEL_CARGO, 
        HISTORICOS.ID_DE_PROCESO ID_DE_PROCESO, 
        HISTORICOS.ANO ANO, 
        HISTORICOS.MES MES, 
        HISTORICOS.PERIODO PERIODO,
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=158
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) PRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=930
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) SUELDOPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=159
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END)+
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=160
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) PRIMAEXTRASEMESTRAL, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=931
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) PRIMASEMESTRAL, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=932
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) PRIMAVAC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=933
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) TRANSPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=934
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) ALIMENPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=935
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) GASTOSREP, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=939
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) BON, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=940
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) VPT, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=936
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) DIASPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=937
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) DIASCOMPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=938
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) DIASLICPRIMADIC, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=942
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) EXTRAS, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=140
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) DESCUENTOS, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=144
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) NETO, 
        SUM(CASE WHEN
                                HISTORICOS.ID_DE_CONCEPTO=150
                       THEN 
                                HISTORICOS.VALOR
                       ELSE
                                0
                       END) BASP,
        SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=170
            THEN 
                      HISTORICOS.VALOR
            ELSE
                      0
            END) PANTIG
FROM HISTORICOS 
  INNER JOIN PERSONAL_HISTORICO 
    ON  HISTORICOS.COMPANIA       = PERSONAL_HISTORICO.COMPANIA 
    AND HISTORICOS.ANO            = PERSONAL_HISTORICO.ANO
    AND HISTORICOS.MES            = PERSONAL_HISTORICO.MES
    AND HISTORICOS.PERIODO        = PERSONAL_HISTORICO.PERIODO
    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO 
                    
WHERE HISTORICOS.COMPANIA       =  s$compania$s
  AND HISTORICOS.ID_DE_CONCEPTO IN (158,930,931,932,933,934,935,936,937,938,939,940,942,140,144,159,150,140,097,163,170, 160) 
  AND (HISTORICOS.ID_DE_PROCESO = s$proceso$s 
       AND HISTORICOS.ANO       = s$anio$s
       AND HISTORICOS.MES       = s$mes$s  
       AND HISTORICOS.PERIODO   = s$periodo$s 
       AND ((SELECT (SUM(  CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 144
                                THEN HISTORICOS.VALOR
                                ELSE 0
                            END)) FROM HISTORICOS) > 0))
GROUP BY    PERSONAL_HISTORICO.CODIGO_ESTABLECIMIENTO, 
            PERSONAL_HISTORICO.NOMBRE_ESTABLECIMIENTO, 
            PERSONAL_HISTORICO.ID_DE_EMPLEADO, 
            PERSONAL_HISTORICO.ID_CENTRO_DE_COSTO, 
            PERSONAL_HISTORICO.DEPENDENCIA,
            PERSONAL_HISTORICO.NOMBRE_DEPENDENCIA, 
            PERSONAL_HISTORICO.NOMBRECOMPLETO, 
            PERSONAL_HISTORICO.NUMERO_DCTO, 
            PERSONAL_HISTORICO.FECHA_DE_INGRESO, 
            PERSONAL_HISTORICO.FECHATERCONTRATO, 
            PERSONAL_HISTORICO.NOMBRE_CENTRO_DE_COSTO, 
            PERSONAL_HISTORICO.NOMBRE_DE_CARGO, 
            HISTORICOS.ID_DE_PROCESO, 
            HISTORICOS.ANO, 
            HISTORICOS.MES, 
            HISTORICOS.PERIODO
