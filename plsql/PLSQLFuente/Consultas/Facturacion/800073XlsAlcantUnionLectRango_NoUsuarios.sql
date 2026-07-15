SELECT 
s$Select$s
FROM (
SELECT * 
FROM (
SELECT

  Tarifas.UsoSuperservicios,
  Estadisticas_Consumo.Consumo,
  Estadisticas_Consumo.ConsumoPromedio,
  CASE
    WHEN UsoSuperservicios='1'
    THEN Estadisticas_Consumo.Estrato
    ELSE '1'
  END AS estrat,
  Estadisticas_Consumo.Rango,
     ESTADISTICAS_CONSUMO.ANO||ESTADISTICAS_CONSUMO.PERIODO as PERIODOCOMPLETO
FROM SP_Estadisticas_Consumo Estadisticas_Consumo
LEFT JOIN SP_Tarifas Tarifas
ON Estadisticas_Consumo.Compania   = Tarifas.Compania
AND Estadisticas_Consumo.Ano       = Tarifas.Ano
AND Estadisticas_Consumo.Periodo   = Tarifas.Periodo
AND Estadisticas_Consumo.Estrato   = Tarifas.Estrato
AND Estadisticas_Consumo.Uso       = Tarifas.Uso
WHERE (TARIFAS.USOSUPERSERVICIOS) IS NOT NULL
AND Estadisticas_Consumo.Compania  =s$compania$s
AND ((PCK_SYSMAN_UTL.FC_STRZERO(Estadisticas_Consumo.Ano,4)
  ||PCK_SYSMAN_UTL.FC_STRZERO(Estadisticas_Consumo.periodo,2)) BETWEEN PCK_SYSMAN_UTL.FC_STRZERO(s$anoInicial$s,4)
  || PCK_SYSMAN_UTL.FC_STRZERO('s$periodoInicial$s',2)
AND PCK_SYSMAN_UTL.FC_STRZERO(s$anoFinal$s,4)
  || PCK_SYSMAN_UTL.FC_STRZERO('s$periodoFinal$s',2))
AND ((Estadisticas_Consumo.Servicio) ='02')
GROUP BY Tarifas.UsoSuperservicios, Estadisticas_Consumo.Rango, Estadisticas_Consumo.Compania, Estadisticas_Consumo.Consumo, Estadisticas_Consumo.ConsumoPromedio, 
ESTADISTICAS_CONSUMO.ANO||ESTADISTICAS_CONSUMO.PERIODO,
  CASE
    WHEN UsoSuperservicios='1'
    THEN Estadisticas_Consumo.Estrato
    ELSE '1'
    END,
  PCK_SYSMAN_UTL.FC_STRZERO(Estadisticas_Consumo.periodo,2)

  )
   PIVOT (SUM(NVL(Consumo,0)+NVL(ConsumoPromedio,0)) FOR PERIODOCOMPLETO IN (s$inPivot$s))
   
UNION

SELECT * FROM (

SELECT
   Estadisticas_Consumo.CANTUSUARIOSALCANTARILLADO,
    Tarifas.UsoSuperservicios,
    CASE WHEN UsoSuperservicios='1' THEN Estadisticas_Consumo.Estrato ELSE '1' END  AS estrat,
    '00'                                                              AS Rango,
    ESTADISTICAS_CONSUMO.ANO||ESTADISTICAS_CONSUMO.PERIODO as PERIODOCOMPLETO
  FROM SP_Estadisticas_Consumo Estadisticas_Consumo
  LEFT JOIN SP_Tarifas Tarifas
  ON (Estadisticas_Consumo.Compania    = Tarifas.Compania)
  AND (Estadisticas_Consumo.Ano        = Tarifas.Ano)
  AND (Estadisticas_Consumo.Periodo    = Tarifas.Periodo)
  AND (Estadisticas_Consumo.Uso        = Tarifas.Uso)
  AND (Estadisticas_Consumo.Estrato    = Tarifas.Estrato)
  WHERE Tarifas.UsoSuperservicios IS NOT NULL
  AND Estadisticas_Consumo.Compania =s$compania$s
  AND PCK_SYSMAN_UTL.FC_STRZERO(Estadisticas_Consumo.Ano,4)
    || PCK_SYSMAN_UTL.FC_STRZERO(Estadisticas_Consumo.periodo,2) BETWEEN PCK_SYSMAN_UTL.FC_STRZERO(s$anoInicial$s,4)
    || PCK_SYSMAN_UTL.FC_STRZERO(s$periodoInicial$s,2) AND PCK_SYSMAN_UTL.FC_STRZERO(s$anoFinal$s,4)
    || PCK_SYSMAN_UTL.FC_STRZERO(s$periodoFinal$s,2)
  AND Estadisticas_Consumo.Servicio                  ='02'
  GROUP BY Estadisticas_Consumo.Compania,
    Tarifas.UsoSuperservicios,
    Estadisticas_Consumo.CANTUSUARIOSALCANTARILLADO,
    ESTADISTICAS_CONSUMO.ANO||ESTADISTICAS_CONSUMO.PERIODO,
    '00'                                                              ,
    CASE UsoSuperservicios
    WHEN '1' THEN Estadisticas_Consumo.Estrato
    ELSE '1' END
  ORDER BY Estadisticas_Consumo.Compania 
)  PIVOT (SUM(CantUsuariosAlcantarillado )FOR PERIODOCOMPLETO IN (s$inPivot$s)))
