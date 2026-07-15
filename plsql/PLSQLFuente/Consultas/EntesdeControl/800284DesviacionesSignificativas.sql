SELECT  CODIGO, 
        '1' "CLASIFICACIÓN VARIACIÓN",
        NOMBRE,
        '' "DETALLE VARIACIÓN"
      , SUM(NUEVO - SANTERIOR) "VALOR VARIACIÓN |PESOS|"
FROM (
SELECT  COMPANIA
      , CODIGO
      , NOMBRE
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , SALDOs$mesAnterior$s SANTERIOR
      , 0 NUEVO
      , SUBSTR(CODIGO,1,1) CLASE
      , CASE
          WHEN SUBSTR(CODIGO,1,1)='0'
            THEN 'z'
              ||CODIGO
            ELSE 
              CODIGO
        END ORDEN
 
FROM (
s$baseBalance$s) 
BASE
UNION ALL
SELECT  COMPANIA
      , CODIGO
      , NOMBRE
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , 0 NUEVO
      , SALDOs$mesComparar$s SANTERIOR
      , SUBSTR(CODIGO,1,1) CLASE
      , CASE
          WHEN SUBSTR(CODIGO,1,1)='0'
            THEN 'z'
              ||CODIGO
            ELSE 
              CODIGO
        END ORDEN
 
FROM (
s$baseBalanceUnion$s ) 
)
WHERE LENGTH(CODIGO)=s$digitos$s
GROUP BY COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE
      , CLASE
      , ORDEN
HAVING SUM(NUEVO - SANTERIOR) BETWEEN s$limiteInferior$s AND s$limiteSuperior$s      
ORDER BY CODIGO
