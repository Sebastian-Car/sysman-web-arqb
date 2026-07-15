WITH DEUDA_TOTAL AS (
  SELECT  F.COMPANIA
        , F.CODIGO
        , SUM(F.TOTAL) TOTALDEUDA
  FROM   IP_FACTURADOS F
  WHERE  F.COMPANIA = '001'
  AND    F.PAGADO = 0
  AND    F.NOCOBRADO = 0
  AND    F.INDEXE = 0
  AND    F.INDCAR = 0
  AND    F.INDEXEOTROS = 0
  AND    F.INDPAGO_ACPAG = 0
     s$stranos$s  
  GROUP BY F.COMPANIA, F.CODIGO) 
SELECT CASE WHEN SUBSTR(U.CODIGO, 1, 2) IN('00')
            THEN 'RURALES'
       WHEN SUBSTR(U.CODIGO, 1, 2) IN('01')
            THEN 'URBANOS'
       ELSE ''
       END TIPO_PREDIO,
        F.PREANO
      , SUM(F.C1)  SUMADEC1
      , SUM(F.C3)  SUMADEC3
      , SUM(F.C2 + F.C4) INTIMPCAR
      , SUM(0) SUMADESOBRETBOM
      , SUM(0) ACUMVIGANT
      , SUM(F.TOTAL)
 SUMATOTAL
      , SUM(F.C13 + F.C14 + F.C15 + F.C16 + F.C17 + F.C18 + F.C19 + F.C20) OTROS
FROM IP_USUARIOS_PREDIAL U
  INNER JOIN IP_FACTURADOS F
    ON  U.COMPANIA      = F.COMPANIA
    AND U.CODIGO        = F.CODIGO
    AND U.NUMERO_ORDEN  = F.NUMERO_ORDEN
       INNER JOIN ANO A
        ON  F.COMPANIA  = A.COMPANIA
        AND F.PREANO    = A.NUMERO
  INNER JOIN DEUDA_TOTAL D 
   ON  U.COMPANIA = D.COMPANIA
   AND U.CODIGO   = D.CODIGO
WHERE U.COMPANIA = s$compania$s
 s$strcodigos$s 
 s$strcedulas$s 
 s$strnombres$s 
 s$strtipopred$s 
 s$strincluye$s 
 s$stranoPago$s 
AND   F.PAGADO = 0
AND   F.NOCOBRADO = 0
AND   F.INDEXE = 0
AND   F.INDCAR = 0
AND   F.INDEXEOTROS = 0
AND   F.INDPAGO_ACPAG = 0
 s$stranos$s  
 s$strvalores$s 
 s$strlotes$s 
 s$strprocesocobro$s
GROUP BY   CASE WHEN SUBSTR(U.CODIGO, 1, 2) IN('00')
            	THEN 'RURALES'
       		WHEN SUBSTR(U.CODIGO, 1, 2) IN('01')
            	THEN 'URBANOS'
       		ELSE ''
       		END, 
          	F.PREANO
ORDER BY PREANO
