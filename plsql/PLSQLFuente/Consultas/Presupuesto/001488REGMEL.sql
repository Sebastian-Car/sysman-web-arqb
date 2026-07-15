SELECT Tipo_comprobPP.Nombre NOMBRETIPO,
  Detalle_comprobante_pptal.Comprobante,
  Tercero.Nombre NOMBRETER,
  PCK_PRESUPUESTO3.FC_GETNOMBRERUBROPREDECESORES(COMPROBANTE_PPTAL.COMPANIA, COMPROBANTE_PPTAL.ANO, Detalle_comprobante_pptal.CUENTA) Nombrerubro,
  Comprobante_pptal.TIPO TIPOCOMP,
  Detalle_comprobante_pptal.TIPO_CPTE,
  Detalle_comprobante_pptal.Tercero,
  Detalle_comprobante_pptal.CUENTA Codigo_cuenta,
  Detalle_comprobante_pptal.CUENTA CUENTADETALLE,
  Detalle_comprobante_pptal.Tipo_cpte_afect,
  Detalle_comprobante_pptal.Cmpte_afectado,
  Detalle_comprobante_pptal.Valor_debito,
  Detalle_comprobante_pptal.Valor_credito,
  (Detalle_comprobante_pptal.valor_debito-Detalle_comprobante_pptal.valor_credito)+(Detalle_comprobante_pptal.modificacion_debito-Detalle_comprobante_pptal.Modificacion_credito)-(Detalle_comprobante_pptal.debito_afectado-Detalle_comprobante_pptal.credito_afectado) AS Valordisponible,
  Detalle_comprobante_pptal.Descripcion,
  to_date(to_char(Detalle_comprobante_pptal.Fecha
, 'dd/mm/yyyy'), 'dd/mm/yyyy') fecha
FROM Comprobante_pptal
LEFT JOIN Tipo_comprobPP
ON Comprobante_pptal.Compania = Tipo_comprobPP.Compania
AND Comprobante_pptal.Tipo    = Tipo_comprobPP.Codigo
LEFT JOIN Detalle_comprobante_pptal
ON Comprobante_pptal.Numero    = Detalle_comprobante_pptal.Comprobante
AND Comprobante_pptal.Tipo     = Detalle_comprobante_pptal.Tipo_cpte
AND Comprobante_pptal.Ano      = Detalle_comprobante_pptal.Ano
AND Comprobante_pptal.Compania = Detalle_comprobante_pptal.Compania
LEFT JOIN Tercero
ON Tercero.Compania                          = Detalle_comprobante_pptal.Compania
AND Tercero.Sucursal                         = Detalle_comprobante_pptal.Sucursal
AND Tercero.Nit                              = Detalle_comprobante_pptal.Tercero
WHERE Detalle_comprobante_pptal.Comprobante IS NOT NULL
AND Comprobante_pptal.Compania               =s$compania$s
AND Comprobante_pptal.Ano                    =s$ano$s
AND Comprobante_pptal.Tipo                   = 's$tipoCpte$s'
AND Comprobante_pptal.Numero BETWEEN s$numeroPptoInicial$s AND s$numeroPptoFinal$s
