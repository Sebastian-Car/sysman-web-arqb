SELECT Tipo_comprobante.Nombre,
  Comprobante_cnt.Numero,
  comprobante_cnt.tipo,
  detalle_comprobante_cnt.comprobante,
  Comprobante_cnt.Fecha,
  Tercero.NOMBRE NombreTercero1,
  Comprobante_cnt.Tercero,
  Comprobante_cnt.Nro_documento COMPROBANTENRO_DOCUMENTO,
  COMPROBANTE_CNT.Fecha_vcn_doc,
  Comprobante_cnt.Vlr_documento,
  Comprobante_cnt.Descripcion,
  Comprobante_cnt.TEXTO,
  Detalle_comprobante_cnt.Nro_documento DETALLENRO_DOCUMENTO,
  Detalle_comprobante_cnt.CuentaPptal,
  Detalle_comprobante_cnt.cuenta Codigo_cuenta,
  PLAN_CONTABLE.NOMBRE
  ||
  CASE
    WHEN NVL(TERCERO.NIT,'')= ''
    THEN ''
    ELSE '/'
  END
  || NVL(TERCERO.NOMBRE,'')
  ||
  CASE
    WHEN NVL(AUXILIAR.CODIGO,'')= ''
    THEN ''
    ELSE '/'
  END
  || NVL(AUXILIAR.NOMBRE,'') AS TOTALNOMBRE,
  Detalle_comprobante_cnt.Valor_debito,
  Detalle_comprobante_cnt.Valor_credito
FROM Detalle_comprobante_cnt
LEFT JOIN Centro_costo
ON Detalle_comprobante_cnt.COMPANIA      = Centro_costo.COMPANIA
AND Detalle_comprobante_cnt.ANO = Centro_costo.ANO
AND Detalle_comprobante_cnt.CENTRO_COSTO = Centro_costo.CODIGO
LEFT JOIN Auxiliar
ON Detalle_comprobante_cnt.COMPANIA = Auxiliar.COMPANIA
AND Detalle_comprobante_cnt.AUXILIAR  = Auxiliar.CODIGO
AND Detalle_comprobante_cnt.ANO  = Auxiliar.ANO
LEFT JOIN Comprobante_cnt
ON Detalle_comprobante_cnt.COMPANIA   = Comprobante_cnt.COMPANIA
AND Detalle_comprobante_cnt.TIPO_CPTE  = Comprobante_cnt.TIPO
AND Detalle_comprobante_cnt.ANO        = Comprobante_cnt.ANO
AND Detalle_comprobante_cnt.COMPROBANTE = Comprobante_cnt.NUMERO
LEFT JOIN Tipo_comprobante
ON Detalle_comprobante_cnt.TIPO_CPTE = Tipo_comprobante.CODIGO
AND Detalle_comprobante_cnt.COMPANIA = Tipo_comprobante.COMPANIA
LEFT JOIN Plan_contable
ON Detalle_comprobante_cnt.CUENTA    = Plan_contable.CODIGO
AND Detalle_comprobante_cnt.ANO      = Plan_contable.ANO
AND Detalle_comprobante_cnt.COMPANIA = Plan_contable.COMPANIA
LEFT JOIN Tercero
ON Detalle_comprobante_cnt.SUCURSAL   = Tercero.SUCURSAL
AND Detalle_comprobante_cnt.TERCERO   = Tercero.NIT
AND Detalle_comprobante_cnt.COMPANIA  = Tercero.COMPANIA
WHERE Detalle_comprobante_cnt.COMPANIA=s$compania$s
AND Detalle_comprobante_cnt.ANO       =s$ano$s
AND Detalle_comprobante_cnt.TIPO_CPTE = 's$tipoCpte$s'
AND Detalle_comprobante_cnt.COMPROBANTE BETWEEN 's$numeroPptoInicial$s' AND 's$numeroPptoFinal$s'
ORDER BY
  CASE
    WHEN detalle_comprobante_cnt.valor_debito <>0
    THEN 'a'
      || codigo_cuenta
    ELSE 'z'
      || codigo_cuenta
  END,
  Detalle_comprobante_cnt.COMPANIA,
  Detalle_comprobante_cnt.ANO,
  Detalle_comprobante_cnt.TIPO_CPTE,
  Detalle_comprobante_cnt.COMPROBANTE,
  Detalle_comprobante_cnt.CONSECUTIVO
