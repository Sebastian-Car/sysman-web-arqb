SELECT 
    PERSONAL_HISTORICO.ANO, 
    PCK_SYSMAN_UTL.FC_NOMBRE_MES(PERSONAL_HISTORICO.MES)  NOMBREMES, 
    PERSONAL_HISTORICO.NUMERO_DCTO, 
    PERSONAL_HISTORICO.NOMBRECOMPLETO, 
    TO_CHAR(PERSONAL_HISTORICO.INGRESO_DISTRITO, 'DD/MM/YYYY') INGRESO_DISTRITO, 
    MAX(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 910
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  DIAST, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 900
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)    SALARIO, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 908
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  GASTOSREP, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 903
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  TRANSP, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 904
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  ALIMENTACION, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 909
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  BONIFICACION, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 906
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  PSERVICIOS, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 907
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  PVAC, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 174
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END) + SUM(CASE WHEN 
                   HISTORICOS.ID_DE_CONCEPTO = 175
               THEN 
                   HISTORICOS.VALOR
               ELSE
                   0
               END)  VAC, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 905
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  PNAVIDAD, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 902
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  EXTRAS, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = 913
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END)  BASECES, 
    SUM(CASE WHEN 
            HISTORICOS.ID_DE_CONCEPTO = s$cptoDoceavasFNA$s 
        THEN 
            HISTORICOS.VALOR
        ELSE
            0
        END) + SUM(CASE WHEN 
                   HISTORICOS.ID_DE_CONCEPTO = 911
               THEN 
                   HISTORICOS.VALOR
               ELSE
                   0
               END) + SUM(CASE WHEN 
                          HISTORICOS.ID_DE_CONCEPTO = 278
                      THEN 
                          HISTORICOS.VALOR
                      ELSE
                          0
                      END) + SUM(CASE WHEN 
                                 HISTORICOS.ID_DE_CONCEPTO = 999
                             THEN 
                                 HISTORICOS.VALOR
                             ELSE
                                 0
                             END)  CONSOLIDADAS, 
    PERSONAL_HISTORICO.NOMBRE_FONDOCESANTIAS 
FROM 
    PERSONAL_HISTORICO 
        INNER JOIN HISTORICOS 
        ON 
            (PERSONAL_HISTORICO.COMPANIA = HISTORICOS.COMPANIA) 
             AND 
            (PERSONAL_HISTORICO.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO) 
            AND 
            (PERSONAL_HISTORICO.ANO = HISTORICOS.ANO) 
              AND 
            (PERSONAL_HISTORICO.MES = HISTORICOS.MES) 
              AND 
            (PERSONAL_HISTORICO.PERIODO = HISTORICOS.PERIODO)               
                AND 
           (PERSONAL_HISTORICO.ID_DE_EMPLEADO = HISTORICOS.ID_DE_EMPLEADO) 
WHERE 
    (
        ((PERSONAL_HISTORICO.COMPANIA) = s$compania$s) 
            AND 
        ((PERSONAL_HISTORICO.ANO) = s$ano$s ) 
            AND 
        ((PERSONAL_HISTORICO.MES) = s$mes$s) 
            --AND 
        --((PERSONAL_HISTORICO.FONDO_CESANTIAS) = 'CES58') 
            AND 
        ((HISTORICOS.ID_DE_CONCEPTO) IN(900, 901, 902, 903, 904, 905, 906, 907, 908, 909, 910, 911, 912, 913, 971, 269, 277, 972, 278, 278, 172, 175, 174, s$cptoDoceavasFNA$s))
    ) 
GROUP BY 
    PERSONAL_HISTORICO.ANO, 
    PCK_SYSMAN_UTL.FC_NOMBRE_MES(PERSONAL_HISTORICO.MES), 
    PERSONAL_HISTORICO.NUMERO_DCTO, 
    PERSONAL_HISTORICO.NOMBRECOMPLETO, 
    PERSONAL_HISTORICO.INGRESO_DISTRITO, 
    PERSONAL_HISTORICO.NOMBRE_FONDOCESANTIAS 
ORDER BY 
    PERSONAL_HISTORICO.NOMBRECOMPLETO
