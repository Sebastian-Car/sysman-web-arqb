SELECT COMPANIA, 
       ID_DE_PROCESO,
       ANO,
       MES, 
       PERIODO,
       ID_DE_EMPLEADO,
       ID_DE_CONCEPTO, 
       VALOR,
       CLASE,
       NOMBRE_CONCEPTO, 
       NOMBRECOMPLETO
 FROM V_ACUMULADOS 
 WHERE COMPANIA = s$compania$s   
 AND ANO= s$ano2$s           
 AND MES= s$mes2$s
 AND PERIODO= s$periodo2$s               
 AND VALOR < 0 
 AND CLASE IN (3, 5, 7, 8)
ORDER BY CLASE,
         NOMBRECOMPLETO
