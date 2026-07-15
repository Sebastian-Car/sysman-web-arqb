SELECT COMPANIA 
      , SUBSTR(CODIGO, 1,1) CLASE  
      , CODIGO  
      , NOMBRE  
 PLAN_CONTABLE_NOMBRE 
      , TERCERO  
      , CENTRO_COSTO  
      , AUXILIAR  
      , REFERENCIA  
      , FUENTE_RECURSOS  
      , (CASE WHEN (NATURALEZA='D' AND SALDOs$mestrabajo$s>=0)   
		      THEN SALDOs$mestrabajo$s   
		      ELSE 0   
		  END)+ (CASE WHEN (NATURALEZA='C' AND SALDOs$mestrabajo$s<0)   
		              THEN - SALDOs$mestrabajo$s  
		              ELSE 0   
		        END) SALDONUEDEBITO  
    , (CASE WHEN (NATURALEZA='C' AND SALDOs$mestrabajo$s>=0)   
		      THEN SALDOs$mestrabajo$s   
		      ELSE 0   
		 END)+ (CASE WHEN (NATURALEZA='D' AND SALDOs$mestrabajo$s<0)   
		             THEN -SALDOs$mestrabajo$s   
		             ELSE 0   
		        END) SALDONUECREDITO   
    , CASE WHEN (SUBSTR(CODIGO,1,1)='0')  
			         THEN 'z' || CODIGO  
			         ELSE CODIGO  
			         END CLASEORDEN 
FROM(s$baseBalance$s) 
WHERE LENGTH(CODIGO) <= s$digitos$s
 s$saldoCeroExt$s
