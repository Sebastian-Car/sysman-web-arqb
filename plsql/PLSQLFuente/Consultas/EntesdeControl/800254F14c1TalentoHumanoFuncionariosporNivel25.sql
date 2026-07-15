SELECT  PERSONAL.SECTOR_SIA                                                     "Sector",
       ESCALAFON.NOMBRE                                                         "Denominacion Del Cargo",
   COUNT(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
                AND FORMANOMBRAMIENTO.IDFORMA='CA'
            THEN FORMANOMBRAMIENTO.IDFORMA
            ELSE NULL END)                                                      "Carrera Administrativa Y/o Empleado Oficial",
     COUNT(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
                  AND FORMANOMBRAMIENTO.IDFORMA='PV' 
            THEN FORMANOMBRAMIENTO.IDFORMA
            ELSE NULL END)                                                      "Provisionalidad", 
    COUNT(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
                  AND FORMANOMBRAMIENTO.IDFORMA='LN' 
            THEN FORMANOMBRAMIENTO.IDFORMA
            ELSE NULL END)                                                      "Libre Nombramiento",
    COUNT(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
                  AND FORMANOMBRAMIENTO.IDFORMA='SN' 
            THEN FORMANOMBRAMIENTO.IDFORMA
            ELSE NULL END)                                                      "Supernumerario",
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
             THEN 1
            ELSE 0 END)                                                         "Total Funcionarios", 
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
             THEN 1
            ELSE 0 END)                                                         "Planta Autorizada", 
            
   SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
             THEN 1
            ELSE 0 END)                                                         "Planta Ocupada",  
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
               AND TO_CHAR(PERSONAL.FECHA_DE_INGRESO,'YYYY')=s$ano$s
             THEN 1
            ELSE 0 END)                                                         "Personal Vinculado",           
     
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=3
           AND TO_CHAR(PERSONAL.FECHA_DE_RETIRO,'YYYY')=s$ano$s
             THEN 1
            ELSE 0 END)                                                         "Personal Desvinculado"         
            
   FROM PERSONAL INNER JOIN  ESCALAFON
        ON  PERSONAL.COMPANIA=ESCALAFON.COMPANIA
        AND PERSONAL.ESCALAFON=ESCALAFON.CODIGO
      LEFT JOIN FORMANOMBRAMIENTO 
       ON   PERSONAL.COMPANIA=FORMANOMBRAMIENTO.COMPANIA
       AND  PERSONAL.DE_CARRERA=FORMANOMBRAMIENTO.IDFORMA
WHERE FORMANOMBRAMIENTO.IDFORMA IN('CA','EO','PV','LN','SN','NOMPROVI','PP','NO','PERDES') 
        AND PERSONAL.ID_DE_TIPO Not In ('99')
        AND (PERSONAL.ESTADO_ACTUAL=1 OR (ESTADO_ACTUAL=3 AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'YYYY'))=s$ano$s AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'MM')) BETWEEN  s$mesInicial$s AND s$mesFinal$s))
 GROUP BY ESCALAFON.NOMBRE,PERSONAL.SECTOR_SIA    
