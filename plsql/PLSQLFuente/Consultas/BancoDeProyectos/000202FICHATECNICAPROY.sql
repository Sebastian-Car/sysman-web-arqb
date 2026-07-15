SELECT BP_FICHA_TECNICA.COMPANIA,         
    BP_FICHA_TECNICA.OBSERVACION OBSERVACION_GENERAL,         
    (CASE BP_FICHA_TECNICA.CUMPLE          
        WHEN 0              
            THEN 'X'          
        ELSE ''      
    END) NO_CUMPLEGENERAL,      
    (CASE BP_FICHA_TECNICA.CUMPLE          
        WHEN -1              
            THEN 'X'          
        ELSE ''      
    END) SI_CUMPLEGENERAL,        
    BP_D_FICHA_TECNICA.CODIGO_DET,        
    BP_FICHA_TECNICA.PROYECTO,        
    BP_FICHA_TECNICA.SECTOR,       
    BP_D_FICHA_TECNICA.SECCION,        
    BP_D_FICHA_TECNICA.ITEM,       
    (CASE BP_D_FICHA_TECNICA.APLICA           
        WHEN 0              
            THEN 'X'          
        ELSE ''      
    END) NO_APLICA,      
    (CASE BP_D_FICHA_TECNICA.APLICA           
        WHEN -1              
            THEN 'X'          
        ELSE ''      
    END) SI_APLICA,         
    (CASE BP_D_FICHA_TECNICA.CUMPLE           
        WHEN 0              
            THEN 'X'          
        ELSE ''      
    END) NO_CUMPLE,      
    (CASE BP_D_FICHA_TECNICA.CUMPLE           
        WHEN -1              
            THEN 'X'          
        ELSE ''      
    END) SI_CUMPLE,      
    BP_D_FICHA_TECNICA.OBSERVACION,         
    PROYECTOS.NOMBREPROYECTO,       
    PROYECTOS.OBJETO     
FROM (BP_FICHA_TECNICA INNER JOIN PROYECTOS  
    ON BP_FICHA_TECNICA.COMPANIA = PROYECTOS.COMPANIA  
    AND BP_FICHA_TECNICA.PROYECTO = PROYECTOS.CODIGO) INNER JOIN BP_D_FICHA_TECNICA  
        ON BP_FICHA_TECNICA.COMPANIA = BP_D_FICHA_TECNICA.COMPANIA          
        AND BP_FICHA_TECNICA.PROYECTO = BP_D_FICHA_TECNICA.PROYECTO           
        AND BP_FICHA_TECNICA.SECTOR = BP_D_FICHA_TECNICA.SECTOR    
WHERE PROYECTOS.COMPANIA = s$compania$s      
    AND PROYECTOS.CODIGO = 's$codigoProyecto$s'  
    AND BP_FICHA_TECNICA.SECTOR = 's$sector$s'     
ORDER BY BP_D_FICHA_TECNICA.CODIGO_DET,  
    BP_FICHA_TECNICA.PROYECTO,  
    BP_FICHA_TECNICA.SECTOR
