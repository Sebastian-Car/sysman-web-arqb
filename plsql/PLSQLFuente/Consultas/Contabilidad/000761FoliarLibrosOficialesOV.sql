SELECT 
  
ROWNUM || ID AS ID 
  
FROM V_Plan_contable 
  
where compania= s$compania$s 
  
AND ROWNUM  <=  (s$numFinal$s - s$numInicial$s) + 1
