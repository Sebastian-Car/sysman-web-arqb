SELECT *   
   FROM   
   (SELECT SUM(V_ACUMULADOS.VALOR) OVER (Partition BY V_ACUMULADOS.ID_DE_CONCEPTO) Total,   
          ID_DE_CONCEPTO ,   
          NOMBRE_CONCEPTO    s$nombreEmpleado$s    ,  
          ANO || '_'||MES ||'_' || PERIODO PER ,   
          VALOR   
        FROM   V_ACUMULADOS   
        WHERE  COMPANIA = s$compania$s   
          AND  ID_DE_EMPLEADO =    s$idEmpleado$s
          AND  ANO BETWEEN    s$anoConsulta$s    AND 9999  
          AND  MES BETWEEN 1 AND 12   
          AND  VALOR NOT IN(0)  
    ) PIVOTHISTORICOS   
   PIVOT ( SUM(VALOR) FOR PER IN (  s$condicionPivot$s   ))   
   ORDER BY 2
