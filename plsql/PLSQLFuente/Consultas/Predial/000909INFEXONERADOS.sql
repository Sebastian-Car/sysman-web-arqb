SELECT UP.CODIGO, 
       TIPOS_DOCUMENTOS.DESCRIPCION TIPO_NIT, 
       UP.NIT, 
       UP.NOMBRE, 
       FA.PREANO, 
       FA.AVALUO, 
       FA.TRPPOR, 
       FA.TOTAL, 
       FA.C1, 
       FA.C2, 
       FA.C3, 
       FA.C4, 
       FA.C5, 
       FA.C6, 
       FA.C7, 
       FA.C8, 
       FA.C9, 
       FA.C10,
       FA.C11, 
       FA.C12, 
       FA.C13, 
       FA.C14, 
       FA.C15, 
       FA.C16, 
       FA.C17, 
       FA.C18, 
       FA.C19, 
       FA.C20, 
       FA.NOCOBRADO, 
       TO_CHAR(FA.PREFEC,'DD/MM/YYYY') PREFEC
FROM IP_FACTURADOS FA INNER JOIN IP_USUARIOS_PREDIAL UP 
      ON FA.COMPANIA     = UP.COMPANIA
     AND FA.CODIGO       = UP.CODIGO
     AND FA.NUMERO_ORDEN = UP.NUMERO_ORDEN
   INNER JOIN TIPOS_DOCUMENTOS
      ON UP.COMPANIA = TIPOS_DOCUMENTOS.COMPANIA
     AND UP.TIPO_NIT = TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
WHERE UP.COMPANIA     = s$compania$s
  AND UP.CODIGO BETWEEN 's$codInicial$s' AND 's$codFinal$s' 
  AND UP.NUMERO_ORDEN = 's$numeroOrden$s'
  AND FA.PREFEC BETWEEN s$fechaIni$s AND s$fechaFin$s 
  AND FA.PREANO_EXONERADO NOT IN (0)
  AND FA.NOCOBRADO        NOT IN (0)
  AND FA.PAGADO           NOT IN (0)
