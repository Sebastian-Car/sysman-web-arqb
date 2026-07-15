SELECT Usuario.COMPANIA,
  usuario.ciclo,
  Usuario.CODIGORUTA,
  Usuario.NOMBRES,
  Usuario.CODIGOINTERNO,
  Usuario.INDDEUDAFINANCIADA,
  SUM(Facturado.DEUDA)                                                                                                           AS SumaDeDeuda,
  SUM(facturado.Valor_FacturadoAnt                            +facturado.DeudaAnt)                                       AS Venia,
  SUM(case when usuario.indDeudaFinanciada in 0 then facturado.deuda-(facturado.Valor_FacturadoAnt+facturado.DeudaAnt) else 0 end ) AS Modificacion,
  SUM(case when usuario.indDeudaFinanciada not in 0 then facturado.deuda -(facturado.Valor_FacturadoAnt+facturado.DeudaAnt) else 0 end ) AS Financiacion,
  SUM(facturado.deuda                                         -(facturado.Valor_FacturadoAnt+facturado.DeudaAnt))    AS Totales
FROM sp_Usuario Usuario
LEFT JOIN (sp_Facturado Facturado
LEFT JOIN sp_Conceptos Conceptos
ON (Facturado.COMPANIA           = Conceptos.COMPANIA)
AND (Facturado.CONCEPTO          = Conceptos.CODIGO))
ON (Usuario.CODIGORUTA           = Facturado.CODIGORUTA)
AND (Usuario.CICLO               = Facturado.CICLO)
AND (Usuario.COMPANIA            = Facturado.COMPANIA)
AND (Usuario.ANO                 = Facturado.ANO)
AND (Usuario.PERIODO             = Facturado.PERIODO)
WHERE (s$ciclo$s
 ((Usuario.BANCOPERANTERIOR) IS NULL)
AND ((Facturado.CONCEPTO) BETWEEN 1 AND 49
OR (Facturado.CONCEPTO) IN (201,202,203,204,205,206,207,246,247,248,249)))
GROUP BY Usuario.COMPANIA,
s$cicloGroupBy$s
  Usuario.CICLO,
  Usuario.CODIGORUTA,
  Usuario.PRIMERAPELLIDO,
  Usuario.SEGUNDOAPELLIDO,
  Usuario.NOMBRES,
  Usuario.CODIGOINTERNO,
  Usuario.INDDEUDAFINANCIADA
HAVING (((SUM(facturado.deuda-(facturado.Valor_FacturadoAnt+facturado.DeudaAnt)))<>0))
