SELECT COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE
      , SUM(SANTERIOR) SANTERIOR
      , SUM(NUEVO) NUEVO
      , CLASE
      , ORDEN 
FROM (
SELECT  COMPANIA
      , CODIGO
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE
      , SALDO11 SANTERIOR
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
      , CENTRO_COSTO
      , TERCERO
      , AUXILIAR
      , REFERENCIA
      , FUENTE_RECURSOS
      , NOMBRE
      , 0 NUEVO
      , SALDO11 SANTERIOR
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
