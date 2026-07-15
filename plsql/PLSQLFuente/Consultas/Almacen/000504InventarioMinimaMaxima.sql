SELECT    COMPANIA, 
                CODIGOELEMENTO, 
                NOMBRELARGO, 
                TIENEMOVIMIENTO,
                DECODE(INVENTARIO.TIENEMOVIMIENTO, 0,'NO', 'SI') AS MOV,
                EXISTENCIA,
                VALORTOTAL, 
                CANTIDADMINIMA, 
                CANTIDADMAXIMA
FROM      INVENTARIO
WHERE     COMPANIA= s$compania$s  
AND       CODIGOELEMENTO BETWEEN 's$elementoDesde$s' AND 's$elementoHasta$s'
s$condicionMostrar$s
s$condicionExistencia$s 
s$orden$s
