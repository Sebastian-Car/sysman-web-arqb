SELECT		COMPANIA,  
					ANO,    
					CODIGO,
					CODIGO ||''|| AUXILIAR_NOMBRE 				     ROMPE,  
					SUBSTR(CODIGO,1,1)       CLASE,   
					SALDOs$mesActual$s 	     SALDO,  
					SALDOs$mesAnterior$s     SALDO_1,  
					CENTRO_COSTO,  
					NOMBRE PNOMBRE,  
					DEBITOs$mesActual$s   	 DEBITO,  
					CREDITOs$mesActual$s	 CREDITO,  
					DEBITOs$mesAnterior$s    DEBITO_1,  
					CREDITOs$mesAnterior$s   CREDITO_1,   
					NATURALEZA,  
					NETOs$mesAnterior$s, 
					(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesAnterior$s >= 0 THEN  SALDOs$mesAnterior$s  ELSE 0 END) + 
					(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesAnterior$s  < 0 THEN -SALDOs$mesAnterior$s  ELSE 0 END) 
					SALDOANTDEBITO, 
					(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesAnterior$s >= 0 THEN  SALDOs$mesAnterior$s  ELSE 0 END) + 
					(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesAnterior$s < 0  THEN -SALDOs$mesAnterior$s  ELSE 0 END) 
					SALDOANTCREDITO, 
					(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesActual$s >= 0   THEN  SALDOs$mesActual$s    ELSE 0 END) + 
					(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesActual$s < 0    THEN -SALDOs$mesActual$s    ELSE 0 END) 
					SALDONUEDEBITO, 
					(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesActual$s >= 0   THEN  SALDOs$mesActual$s    ELSE 0 END) + 
					(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesActual$s  < 0   THEN -SALDOs$mesActual$s    ELSE 0 END) 
					SALDONUECREDITO		 
        FROM 	(s$consultaBase$s ) 
        WHERE 		LENGTH(CODIGO) = 1 
        AND 	   ((AUXILIAR IS NOT NULL 
        AND      AUXILIAR      BETWEEN 's$auxiliarInicial$s' AND 's$auxiliarFinal$s') 
        OR      (TERCERO 	  IS NULL 
       AND      CENTRO_COSTO       IS NULL 
       AND      AUXILIAR 	  IS NULL) 
) 
      s$condicionSaldoCero$s
