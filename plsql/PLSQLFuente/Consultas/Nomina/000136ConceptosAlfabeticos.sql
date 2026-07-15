SELECT  
  NOMBRE_CONCEPTO, 
  ID_DE_CONCEPTO, 
  CASE CLASE  
    WHEN 1 THEN 'Informativo' 
    WHEN 2 THEN 'Otros Informativos'  
    WHEN 3 THEN 'Remuneración' 
    WHEN 4 THEN 'Total Devengado'  
    WHEN 5 THEN 'Descuento' 
    WHEN 6 THEN 'Total Deducido' 
    WHEN 7 THEN 'Neto a Pagar' 
    WHEN 8 THEN 'Contable' 
    WHEN 9 THEN 'Anticipados' 
    WHEN 99 THEN 'Bases' 
  END CLASE, 
  CASE DENOVEDAD WHEN 0 THEN ' ' WHEN -1  THEN 'x' END DENOVEDAD 
FROM CONCEPTOS 
WHERE CONCEPTOS.COMPANIA=s$compania$s ORDER BY s$orden$s
