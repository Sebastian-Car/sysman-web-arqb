SELECT    DISTINCT
           CODIGO, 
           NOMBRE 
  FROM     PLAN_CONTABLE   
  WHERE    COMPANIA = 's$compania$s'   
  AND      ANO      =  s$anio$s       
  AND      MOVIMIENTO <> 0   
  AND      (CODIGO_NIIF < '0' OR CODIGO_NIIF IS NULL)   
  ORDER BY CODIGO
