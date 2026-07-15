
WITH TMP_EXENTOS 
AS ( 
      SELECT CODIGO, 
             PREANO, 
             C1_EXE AS C1, 
             C3_EXE AS C3, 
             C2_EXE AS C2, 
             C4_EXE AS C4, 
             C13_EXE AS C13, 
             C14_EXE AS C14,
             C15_EXE AS C15, 
             C16_EXE AS C16,
             C17_EXE AS C17, 
             C18_EXE AS C18, 
             C19_EXE AS C19, 
             C20_EXE AS C20, 
             C1_EXE + C2_EXE + C3_EXE + C4_EXE + C13_EXE + C14_EXE + C15_EXE + C16_EXE + C17_EXE + C18_EXE + C19_EXE + C20_EXE AS TOTALEX
      FROM   IP_FACTURADOS
      WHERE COMPANIA     = s$compania$s
        AND CODIGO 	BETWEEN 's$codInicial$s' AND 's$codFinal$s'
	AND NUMERO_ORDEN = 's$numeroOrden$s'
        AND TRUNC(IP_FACTURADOS.PREFEC) BETWEEN s$fechaIni$s AND s$fechaFin$s

        AND (C1_EXE + C2_EXE + C3_EXE + C4_EXE + C13_EXE + C14_EXE + C15_EXE + C16_EXE + C17_EXE + C18_EXE + C19_EXE + C20_EXE ) NOT IN (0)
)
SELECT CODIGO,
       PREANO, 
       C1, 
       C2, 
       C3, 
       C4, 
       C13, 
       C14, 
       C15, 
       C16, 
       C17, 
       C18, 
       C19, 
       C20, 
       TOTALEX
FROM TMP_EXENTOS
WHERE TMP_EXENTOS.CODIGO BETWEEN 's$codInicial$s' AND 's$codFinal$s'
  AND TOTALEX NOT IN (0)
ORDER BY CODIGO, PREANO
