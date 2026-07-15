SELECT TO_CHAR(VIGENCIA) Vigencia,                                                                                
       PROGRAMADO_TR1_FIN + PROGRAMADO_TR2_FIN + PROGRAMADO_TR3_FIN + PROGRAMADO_TR4_FIN "Cantidad Programada", 
       EJECUTADO_TR1_FIN  + EJECUTADO_TR2_FIN  + EJECUTADO_TR3_FIN  + EJECUTADO_TR4_FIN  "Cantidad Ejecutada",  
       CASE WHEN PROGRAMADO_TR1_FIN + PROGRAMADO_TR2_FIN + PROGRAMADO_TR3_FIN + PROGRAMADO_TR4_FIN <> 0 
        AND EJECUTADO_TR1_FIN  + EJECUTADO_TR2_FIN  + EJECUTADO_TR3_FIN  + EJECUTADO_TR4_FIN <> 0
        THEN  ((EJECUTADO_TR1_FIN  + EJECUTADO_TR2_FIN  + EJECUTADO_TR3_FIN  + EJECUTADO_TR4_FIN) /
              (PROGRAMADO_TR1_FIN + PROGRAMADO_TR2_FIN + PROGRAMADO_TR3_FIN + PROGRAMADO_TR4_FIN)) * 100
        ELSE 0
        END "Porcentaje Ejecución",
        TO_CHAR(FECHA_INICIAL_FIN,'DD/MM/YYYY')"Fecha Inicial",
        TO_CHAR(FECHA_FINAL_FIN,'DD/MM/YYYY')"Fecha Final",    
        PROGRAMADO_TR1_FIN "Trimestre 1 Programado", 
        EJECUTADO_TR1_FIN  "Trimestre 1 Ejecutado",   
        CASE WHEN PROGRAMADO_TR1_FIN <> 0 AND EJECUTADO_TR1_FIN <> 0 
          THEN (EJECUTADO_TR1_FIN / PROGRAMADO_TR1_FIN) * 100 
          ELSE 0
        END "% Ejecución 1",                 
        PROGRAMADO_TR2_FIN "Trimestre 2 Programado", 
        EJECUTADO_TR2_FIN "Trimestre 2 Ejecutado",            
        CASE WHEN PROGRAMADO_TR2_FIN <> 0 AND EJECUTADO_TR2_FIN <> 0 
          THEN (EJECUTADO_TR2_FIN / PROGRAMADO_TR2_FIN) * 100
          ELSE 0
        END "% Ejecución 2",                      
       PROGRAMADO_TR3_FIN "Trimestre 3 Programado",
       EJECUTADO_TR3_FIN "Trimestre 3 Ejecutado" ,                        
       CASE WHEN  EJECUTADO_TR3_FIN <> 0 AND PROGRAMADO_TR3_FIN <> 0
        THEN (EJECUTADO_TR3_FIN / PROGRAMADO_TR3_FIN) * 100 
        ELSE 0
      END "% Ejecución 3",    
      PROGRAMADO_TR4_FIN "Trimestre 4 Programado",
      EJECUTADO_TR4_FIN "Trimestre 4 Ejecutado",              
      CASE WHEN  PROGRAMADO_TR4_FIN <> 0 AND EJECUTADO_TR4_FIN <> 0
        THEN (EJECUTADO_TR4_FIN / PROGRAMADO_TR4_FIN) * 100 
        ELSE 0
      END "% Ejecución 4" ,             
      POBLACION_OBJETIVO "Población Objetivo", 
      UBICACION_GEOGRAFICA "Ublicación Geográfica", 
      IMPACTO_SOCIAL    "Impacto Social"                           
FROM   PI_PROGRAMACION_FISICA                                                                    
WHERE  COMPANIA         = s$compania$s                                             
 AND  TIPO             = 's$tipo$s'                                                   
 AND  NUMERO           =  s$numero$s                                                 
 AND  ID_PLAN          ='s$idPlan$s'                                                  
  AND  VIGENCIA_INICIAL = s$vigencia$s
