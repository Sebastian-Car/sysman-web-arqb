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
SELECT  U.CODIGO
      , U.M_INMOBILIARIA  
      , U.NIT
      , U.NOMBRE
      , U.DIRECCION
      , U.FECHACALCULO
      , F.PREANO
      , F.AVALUO
      , F.TRPCOD
      , F.C1 
      , F.C3
      , (F.C2 + F.C4) INTIMPCAR
      , (F.C13 + F.C14 + F.C15 + F.C16 + F.C17 + F.C18 + F.C19 + F.C20) OTROS
      , 0 ACUMVIGANT
      , 0 SOBRETBOM
      , F.TOTAL
      ,TO_CHAR(( LAST_DAY('01/' ||
          CASE WHEN A.MESESAMNISTIA_PREDIAL  = 0 
            THEN 12 
            ELSE A.MESESAMNISTIA_PREDIAL
            END 
        ||'/'||F.PREANO) + 1),'DD/MM/YYYY')  FECHA 
       , D.TOTALDEUDA  
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
