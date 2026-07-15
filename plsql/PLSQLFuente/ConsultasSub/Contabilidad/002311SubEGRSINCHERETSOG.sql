 SELECT 
    comprobante_cntretencion.COMPANIA, 
    comprobante_cntretencion.CODIGORETENCION, 
    comprobante_cntretencion.TIPO, 
    comprobante_cntretencion.VALORBASE, 
    comprobante_cntretencion.NUMERO, 
    comprobante_cntretencion.PORCIVA, 
    retenciones.NOMBRE, 
    comprobante_cntretencion.ANO, 
    comprobante_cntretencion.VALOR AS VALOR_RETENIDO, 
    retenciones.PCT_APLICAR AS POR_APLIC
FROM 
    comprobante_cntretencion 
    INNER JOIN retenciones ON (comprobante_cntretencion.COMPANIA = retenciones.COMPANIA) 
    AND (comprobante_cntretencion.ANO = retenciones.ANO) 
    AND (comprobante_cntretencion.TIPORETENCION = retenciones.TIPO) 
    AND (comprobante_cntretencion.CODIGORETENCION = retenciones.CODIGO)
    WHERE COMPROBANTE_CNTRETENCION.COMPANIA = $P{PR_COMPANIA}
    AND COMPROBANTE_CNTRETENCION.ANO    = $P{PR_ANO}
    AND COMPROBANTE_CNTRETENCION.TIPO   = $P{PR_TIPO}
    AND COMPROBANTE_CNTRETENCION.NUMERO BETWEEN $P{PR_COMPROBANTE} AND $P{PR_COMPROBANTE}