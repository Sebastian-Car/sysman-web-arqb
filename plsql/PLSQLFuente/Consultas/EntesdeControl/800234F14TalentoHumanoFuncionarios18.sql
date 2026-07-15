SELECT ESCALAFON.NOMBRE                                                         "Denominacion Del Cargo",
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
                  AND PERSONAL.AREAMISOADM='MI' 
            THEN 1
            ELSE 0 END)                                                         "Total Area Misional",
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
                  AND PERSONAL.AREAMISOADM='AD' 
            THEN 1
            ELSE 0 END)                                                         "Total Area Administrativa", 
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
             THEN 1
            ELSE 0 END)                                                         "Planta Autorizada", 
            
   SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
             THEN 1
            ELSE 0 END)                                                         " Planta Ocupada",  
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=1
               AND TO_CHAR(PERSONAL.FECHA_DE_INGRESO,'YYYY')=:s$ano$s
             THEN 1
            ELSE 0 END)                                                         "Personal Vinculado",           
     
    SUM(CASE WHEN PERSONAL.ESTADO_ACTUAL=3
           AND TO_CHAR(PERSONAL.FECHA_DE_RETIRO,'YYYY')=:s$ano$s
             THEN 1
            ELSE 0 END)                                                         " Personal Desvinculado"         
            
   FROM PERSONAL INNER JOIN  ESCALAFON
        ON  PERSONAL.COMPANIA=ESCALAFON.COMPANIA
        AND PERSONAL.ESCALAFON=ESCALAFON.CODIGO
      LEFT JOIN FORMANOMBRAMIENTO 
       ON   PERSONAL.COMPANIA=FORMANOMBRAMIENTO.COMPANIA
       AND  PERSONAL.DE_CARRERA=FORMANOMBRAMIENTO.IDFORMA
WHERE FORMANOMBRAMIENTO.IDFORMA IN('CA','EO','PV','LN','SN','NOMPROVI','PP','NO','PERDES') 
        AND PERSONAL.ID_DE_TIPO Not In ('99')
        --AND (TO_NUMBER(TO_CHAR(PERSONAL.FECHA_DE_RETIRO,'YYYY'))>=:s$ano$s OR TO_NUMBER(TO_CHAR(PERSONAL.FECHA_DE_RETIRO,'YYYY')) IS NULL)
        --AND TO_NUMBER(TO_CHAR(PERSONAL.FECHA_DE_INGRESO,'YYYY'))<=:s$ano$s
 GROUP BY ESCALAFON.NOMBRE    
