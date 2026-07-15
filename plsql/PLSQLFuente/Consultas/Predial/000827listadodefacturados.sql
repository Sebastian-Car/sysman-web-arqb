SELECT 
    R.PRECOD CODIGO,
    U.NOMBRE,
    U.DIRECCION, 
    R.DOCNUM MAXDEDOCNUM, 
    R.PREVAL ULTIMODETOTAL
FROM IP_USUARIOS_PREDIAL U 
    INNER JOIN IP_RECIBOS_DE_PAGO R 
        ON  U.COMPANIA      = R.COMPANIA
        AND U.RECIBO_ACTUAL = R.DOCNUM  
    INNER JOIN IP_FACTURADOS FAC
       ON  U.COMPANIA     = FAC.COMPANIA
       AND U.CODIGO       = FAC.CODIGO
       AND U.NUMERO_ORDEN = FAC.NUMERO_ORDEN
WHERE U.COMPANIA               = s$compania$s
  AND U.NUMERO_ORDEN           = 's$numeroOrden$s' 
  AND U.CODIGO                 BETWEEN 's$codigoInicial$s'    AND 's$codigoFinal$s' 
  AND U.DIRECCION              BETWEEN 's$direccionInicial$s' AND 's$direccionFinal$s'
  AND U.NOMBRE                 BETWEEN 's$nombreInicial$s'    AND 's$nombreFinal$s' 
  AND U.NIT                    BETWEEN 's$nitInicial$s'       AND 's$nitFinal$s' 
  AND U.PAGO_ANO               BETWEEN 's$anoInicial$s'       AND 's$anoFinal$s' 
  AND U.TOTAL                  BETWEEN  s$valorInferior$s       AND s$valorSuperior$s
  AND U.INDBORRADO             IN (0) 
  AND U.CODIGO_NO_ACTIVO       IN (0) 
  AND U.IND_PROCESOJUD         IN (0)
  AND R.PAGO                   IN (0)
  AND FAC.PAGADO               IN (0)
  AND FAC.NOCOBRADO            IN (0)
  AND NVL(FAC.INDPAGO_ACPAG,0) IN (0)
  s$ordenamiento$s 
