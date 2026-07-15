SELECT Detalle_comprobante_pptal.*,
  plan_presupuestal.NOMBRE,
  Tercero.NIT    AS CedulaTercero,
  Tercero.NOMBRE AS NombreTercero,
  Tercero.NOMBRE,
  Tercero_1.NOMBRE AS NombreTercero1,
  Tercero_1.DIRECCION,
  Tipo_comprobPP.NOMBRE,
  Comprobante_pptal.*,
  Tercero_1.NOMBRE,
  Tercero_2.NOMBRE,
  CASE WHEN detalle_comprobante_pptal.valor_debito=0 THEN detalle_comprobante_pptal.valor_credito ELSE detalle_comprobante_pptal.valor_debito END ValorComprobante,
  Tercero.DIRECCION,
  Tercero.TELEFONOS,
  Detalle_comprobante_pptal.DESCRIPCION,
  Detalle_comprobante_pptal.VALOR_DEBITO  AS Credito,
  Detalle_comprobante_pptal.VALOR_CREDITO AS ContraCredito,
  Detalle_comprobante_pptal.FECHA         AS fecha,
  plan_presupuestal.CODIGO_EQUIV,
  plan_presupuestal.DESTINO,
  plan_presupuestal.NIVEL1,
  plan_presupuestal.NIVEL2,
  plan_presupuestal.NIVEL3,
  plan_presupuestal.NIVEL4,
  plan_presupuestal.NIVEL5,
  plan_presupuestal.NIVEL6,
  plan_presupuestal.NIVEL7,
  CASE WHEN NVL(plan_presupuestal.Nivel1,'')<>'' THEN plan_presupuestal.Nivel1 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel2,'')<>'' THEN plan_presupuestal.Nivel2 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel3,'')<>'' THEN plan_presupuestal.Nivel3 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel4,'')<>'' THEN plan_presupuestal.Nivel4 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel5,'')<>'' THEN plan_presupuestal.Nivel5 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel6,'')<>'' THEN plan_presupuestal.Nivel6 || ' ' ELSE '' END 
  || CASE WHEN NVL(plan_presupuestal.Nivel7,'')<>'' THEN plan_presupuestal.Nivel7 || ' ' ELSE '' END  AS Rubro,
  Detalle_comprobante_pptal.tipo_cpte_afect || ' ' || Detalle_comprobante_pptal.Cmpte_afectado  AS Afecta,
  CASE WHEN Comprobante_pptal.Destino='F' THEN 'Funcionamiento' ELSE CASE WHEN Comprobante_pptal.Destino='I' THEN 'Inversión' ELSE 'Funcionamiento / Inversión' END END GastosDe,
  plan_presupuestal.CODIGO_EQUIV,
  Comprobante_pptal.FECHA AS FechaComp,
  Comprobante_pptal.FECHA,
  Comprobante_pptal.DEBITO_AFECTADO,
  Detalle_comprobante_pptal.COMPROBANTE AS NumeroComp,
  Detalle_comprobante_pptal.TIPO_CPTE   AS TipoComp,
  Comprobante_pptal.Texto || ' ' || Comprobante_pptal.Nro_documento AS ParaRespaldar,
  
  CASE WHEN Detalle_comprobante_pptal.Centro_costo='9999999999' THEN '' ELSE Detalle_comprobante_pptal.Centro_costo END CodCentro,
  
  CASE WHEN Detalle_comprobante_pptal.Tercero ='99999999999' THEN '' ELSE Detalle_comprobante_pptal.Tercero END CodTercero,
  
  CASE WHEN Detalle_comprobante_pptal.Auxiliar ='9999999999999999' THEN '' ELSE Detalle_comprobante_pptal.Auxiliar END CodAuxiliar,
  
  Centro_costo.NOMBRE                                                                                  AS Expr1,
  Auxiliar.NOMBRE                                                                                      AS Expr2,
  Plan_presupuestal.nombre
  ||(CASE WHEN (CASE WHEN Detalle_comprobante_pptal.Centro_costo='9999999999' THEN '' ELSE Detalle_comprobante_pptal.Centro_costo END)='' THEN '' ELSE ' / ' END  
  || Centro_costo.Nombre)
  ||(CASE WHEN (CASE WHEN Detalle_comprobante_pptal.Tercero ='99999999999' THEN '' ELSE Detalle_comprobante_pptal.Tercero END)='' THEN '' ELSE ' / ' END
  || Tercero.Nombre)
  ||(CASE WHEN (CASE WHEN Detalle_comprobante_pptal.Auxiliar ='9999999999999999' THEN '' ELSE Detalle_comprobante_pptal.Auxiliar END)='' THEN '' ELSE ' / ' END
  || Auxiliar.Nombre) AS TotalNombre,
  Auxiliar.NOMBRE,
  Centro_costo.NOMBRE,
  (Detalle_comprobante_pptal_1.valor_debito - Detalle_comprobante_pptal_1.valor_credito)+(Detalle_comprobante_pptal_1.modificacion_debito - Detalle_comprobante_pptal_1.Modificacion_credito)-(Detalle_comprobante_pptal_1.debito_afectado - Detalle_comprobante_pptal_1.credito_afectado) AS Valordisponible,
  Detalle_comprobante_pptal.COMPANIA                                                                                                                                                                                                                                                 AS ParCompania,
  Detalle_comprobante_pptal.ANO                                                                                                                                                                                                                                                      AS ParAno,
  Detalle_comprobante_pptal.TIPO_CPTE                                                                                                                                                                                                                                                AS ParTipo_cpte,
  Detalle_comprobante_pptal.COMPROBANTE                                                                                                                                                                                                                                              AS ParComprobante,
  Detalle_comprobante_pptal.CONSECUTIVO                                                                                                                                                                                                                                              AS ParConsecutivo,
  Detalle_comprobante_pptal.CUENTA                                                                                                                                                                                                                                                   AS ParCuenta,
  comprobante_pptal.Descripcion || ' - ' || comprobante_pptal.Nro_documento AS TipoYNumeroDeCompromiso,
  Comprobante_pptal.Descripcion || ' - ' || Comprobante_pptal.Nro_documento AS ObjetoCompleto,
  Detalle_comprobante_pptal.DESCRIPCION AS Obj,
  Comprobante_pptal.TEXTO               AS TEXTO,
  Comprobante_pptal.TIPOCONTRATO,
  Comprobante_pptal.NUMEROCONTRATO,
  plan_presupuestal.CODIGO,
  TipoOrdenDeCompra.NOMBRE         AS Tipodecontratacion,
  Comprobante_pptal.NUMEROCONTRATO AS NumeroCon,
  OrdenDeCompra.OBJETOCONTRATO     AS ObjetoContratacion,
  Comprobante_pptal.FECHA,
  Tipo_comprobPP.NOMBRE,
  plan_presupuestal.CODIGO,
  Comprobante_pptal.TEXTO,
  Comprobante_pptal.TERCERO,
  Detalle_comprobante_pptal.valor_debito - Detalle_comprobante_pptal.DEBITO_AFECTADO AS saldocomprobante,
  Tercero.NOMBRE,
  Comprobante_pptal.DESCRIPCION || ' ' || Comprobante_pptal.texto   AS objeto,
  TO_CHAR(Comprobante_pptal.FECHA, 'MONTH') AS mes,
  Tercero_1.NOMBRE AS NomTerceroCom,
  Tercero_1.NIT AS NitTercComp,
  CASE WHEN Comprobante_pptal.Destino='F' THEN 'Funcionamiento' ELSE CASE WHEN Comprobante_pptal.Destino='I' THEN 'Inversión' ELSE CASE WHEN Comprobante_pptal.Destino='A' THEN 'Ambos' ELSE CASE WHEN Comprobante_pptal.Destino='G' THEN 'Gastos Operacionales' ELSE '' END END END END  DesCom, 
  Tipo_comprobPP.NOMBRE                                                                                                                                                                                                      AS NomP
FROM ((((Comprobante_pptal
LEFT JOIN Tipo_comprobPP
ON (Comprobante_pptal.COMPANIA = Tipo_comprobPP.COMPANIA)
AND (Comprobante_pptal.TIPO    = Tipo_comprobPP.CODIGO))
LEFT JOIN Tercero  Tercero_1
ON (Comprobante_pptal.COMPANIA  = Tercero_1.COMPANIA)
AND (Comprobante_pptal.TERCERO  = Tercero_1.NIT)
AND (Comprobante_pptal.SUCURSAL = Tercero_1.SUCURSAL))
LEFT JOIN TipoOrdenDeCompra
ON (Comprobante_pptal.COMPANIA      = TipoOrdenDeCompra.COMPANIA)
AND (Comprobante_pptal.TIPOCONTRATO = TipoOrdenDeCompra.CODIGO))
LEFT JOIN OrdenDeCompra
ON (Comprobante_pptal.TIPOCONTRATO    = OrdenDeCompra.CLASEORDEN)
AND (Comprobante_pptal.NUMEROCONTRATO = OrdenDeCompra.NUMERO)
AND (Comprobante_pptal.COMPANIA       = OrdenDeCompra.COMPANIA))
LEFT JOIN ((((((Tercero
RIGHT JOIN Detalle_comprobante_pptal
ON (Tercero.COMPANIA  = Detalle_comprobante_pptal.COMPANIA)
AND (Tercero.SUCURSAL = Detalle_comprobante_pptal.SUCURSAL)
AND (Tercero.NIT      = Detalle_comprobante_pptal.TERCERO))
LEFT JOIN Detalle_comprobante_pptal  Detalle_comprobante_pptal_1
ON (Detalle_comprobante_pptal.CONSECUTIVO      = Detalle_comprobante_pptal_1.CONSECUTIVO)
AND (Detalle_comprobante_pptal.TIPO_CPTE_AFECT = Detalle_comprobante_pptal_1.TIPO_CPTE)
AND (Detalle_comprobante_pptal.CMPTE_AFECTADO  = Detalle_comprobante_pptal_1.COMPROBANTE)
AND (Detalle_comprobante_pptal.COMPANIA        = Detalle_comprobante_pptal_1.COMPANIA)
AND (Detalle_comprobante_pptal.ANO             = Detalle_comprobante_pptal_1.ANO)
AND (Detalle_comprobante_pptal.CUENTA          = Detalle_comprobante_pptal_1.CUENTA))
LEFT JOIN tercero  Tercero_2
ON (Detalle_comprobante_pptal_1.COMPANIA  = Tercero_2.COMPANIA)
AND (Detalle_comprobante_pptal_1.TERCERO  = Tercero_2.NIT)
AND (Detalle_comprobante_pptal_1.SUCURSAL = Tercero_2.SUCURSAL))
LEFT JOIN Auxiliar
ON (Detalle_comprobante_pptal.AUXILIAR  = Auxiliar.CODIGO)
AND (Detalle_comprobante_pptal.COMPANIA = Auxiliar.COMPANIA))
LEFT JOIN Centro_costo
ON (Detalle_comprobante_pptal.CENTRO_COSTO = Centro_costo.CODIGO)
AND (Detalle_comprobante_pptal.COMPANIA    = Centro_costo.COMPANIA))
LEFT JOIN plan_presupuestal
ON (Detalle_comprobante_pptal.COMPANIA       = plan_presupuestal.COMPANIA)
AND (Detalle_comprobante_pptal.ANO           = plan_presupuestal.ANO)
AND (Detalle_comprobante_pptal.CUENTA        = plan_presupuestal.codigo))
ON (Comprobante_pptal.NUMERO                 = Detalle_comprobante_pptal.COMPROBANTE)
AND (Comprobante_pptal.TIPO                  = Detalle_comprobante_pptal.TIPO_CPTE)
AND (Comprobante_pptal.ANO                   = Detalle_comprobante_pptal.ANO)
AND (Comprobante_pptal.COMPANIA              = Detalle_comprobante_pptal.COMPANIA)
WHERE Comprobante_pptal.COMPANIA         = s$compania$s
AND Comprobante_pptal.ANO                = s$ano$s
AND Comprobante_pptal.TIPO               = 's$tipoCpte$s'
AND Comprobante_pptal.NUMERO BETWEEN s$numeroPptoInicial$s AND s$numeroPptoFinal$s
