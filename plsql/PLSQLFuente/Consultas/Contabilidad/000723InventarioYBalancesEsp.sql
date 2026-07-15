SELECT SALDOAUX.COMPANIA,  
       LPAD((CASE WHEN SUBSTR(SALDOAUX.CODIGO, 1, 1) = '0' THEN 'Z' || SALDOAUX.CODIGO ELSE SALDOAUX.CODIGO END), 1)  CLASEORDEN,  
       SUBSTR(SALDOAUX.CODIGO, 1, 1)  CLASE,  
       SALDOAUX.CODIGO,  
       SALDOAUX.NOMBRE,  
       CASE WHEN SALDOAUX.NATURALEZA = 'C' AND  SALDOAUX.SALDOs$mesTrabajo$s >= 0 
            THEN  SALDOAUX.SALDOs$mesTrabajo$s
            ELSE 0
            END +  CASE WHEN SALDOAUX.NATURALEZA = 'D' AND  SALDOAUX.SALDOs$mesTrabajo$s < 0 
                        THEN  -  SALDOAUX.SALDOs$mesTrabajo$s 
                        ELSE 0
                        END  SALDONUECREDITO,  
       CASE WHEN SALDOAUX.NATURALEZA = 'D' AND  SALDOAUX.SALDOs$mesTrabajo$s >= 0 
            THEN  SALDOAUX.SALDOs$mesTrabajo$s 
            ELSE 0 
            END + CASE WHEN SALDOAUX.NATURALEZA = 'C' AND  SALDOAUX.SALDOs$mesTrabajo$s < 0
                       THEN -  SALDOAUX.SALDOs$mesTrabajo$s
                       ELSE 0 
                       END  SALDONUEDEBITO,  
       CASE WHEN  SUBSTR(SALDOAUX.CODIGO, 1, 1)  = '0' 
            THEN 'Z' || SALDOAUX.CODIGO 
            ELSE SALDOAUX.CODIGO 
            END  ORDEN,  
       SALDOAUX.ORDEN ORDENCUENTAS,
       SALDOAUX.REFERENCIA,
       SALDOAUX.FUENTE_RECURSOS
 
FROM (s$baseBalance$s) SALDOAUX 
WHERE LENGTH(SALDOAUX.CODIGO) <= s$digitos$s
  s$condicion$s
