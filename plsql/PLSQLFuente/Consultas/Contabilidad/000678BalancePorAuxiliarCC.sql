SELECT 	
        SUBSTR(CODIGO,1,1)              CLASE,  
				CODIGO, 
				CODIGO ||''|| AUXILIAR_NOMBRE 	ROMPE, 
				SALDOs$mesActual$s              SALDO,  
				COMPANIA,  
				ANO,  
				SALDOs$mesAnterior$s 			      SALDO_1,
				CENTRO_COSTO,  
				NOMBRE PNOMBRE,  
				TERCERO,    
				AUXILIAR,  
				DEBITOs$mesActual$s             DEBITO,  
				CREDITOs$mesActual$s            CREDITO,  
				DEBITOs$mesAnterior$s           DEBITO_1, 
				CREDITOs$mesAnterior$s          CREDITO_1 ,  
				NATURALEZA,   
				(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesAnterior$s >= 0 THEN  SALDOs$mesAnterior$s  ELSE 0 END) + 
				(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesAnterior$s  < 0 THEN -SALDOs$mesAnterior$s  ELSE 0 END) 
				SALDOANTDEBITO, 
				(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesAnterior$s >= 0 THEN  SALDOs$mesAnterior$s  ELSE 0 END) + 
				(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesAnterior$s  < 0 THEN -SALDOs$mesAnterior$s  ELSE 0 END) 
				SALDOANTCREDITO, 
				(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesActual$s >= 0   THEN  SALDOs$mesActual$s    ELSE 0 END) + 
				(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesActual$s  < 0   THEN -SALDOs$mesActual$s    ELSE 0 END) 
				SALDONUEDEBITO, 
				(CASE WHEN NATURALEZA = 'C' AND SALDOs$mesActual$s >= 0   THEN  SALDOs$mesActual$s    ELSE 0 END) + 
				(CASE WHEN NATURALEZA = 'D' AND SALDOs$mesActual$s  < 0   THEN -SALDOs$mesActual$s    ELSE 0 END) 
				SALDONUECREDITO,   
				CASE WHEN SUBSTR(CODIGO,1,1) = '0' THEN 'Z'||CODIGO ELSE CODIGO END ORDEN, 
				SUBSTR((CASE WHEN SUBSTR(CODIGO,1,1) = '0'  THEN 'Z'||CODIGO ELSE CODIGO END),1,1) CLASEORDEN,  
				TERCERO_NOMBRE TNOMBRE,  
				AUXILIAR_NOMBRE ANOMBRE,  
				CENTRO_COSTO_NOMBRE CNOMBRE,  
 				CODIGO 
				||''||CASE WHEN CENTRO_COSTO IS NULL THEN ' ' ELSE '' END 
				||''||CASE WHEN TERCERO IS NULL      THEN ' ' ELSE '' END 
                ||''||CASE WHEN AUXILIAR IS NULL     THEN ' ' ELSE '' END 
				||''||NOMBRE ORDENCUENTAS
        FROM   (s$consultaBase$s)     
    s$condicionReporte$s  
    s$condicionSaldoCero$s
