MERGE INTO CONSULTAS FIN USING (SELECT '001523COMUPC' INFORME ,TO_CLOB(q'[SELECT 
  Comprobante_cnt.COMPANIA AS Comp,
  Comprobante_cnt.Numero,
  TO_CHAR(Comprobante_cnt.Fecha, 'DD/MM/YYYY')  FECHA,
  Comprobante_cnt.ANO     ANOCOMP,
  Comprobante_cnt.TIPO    TIPOCOMP,
  Comprobante_cnt.NUMERO  NUMCOMP,
  Tercero_1.NOMBRE,
  concat(concat(comprobante_cnt.tercero,'-'), tercero.digitoverificacion) as TERCERO,
  Tercero_1.TELEFONOS AS TELTERCERO,
  Tercero_1.DIRECCION AS DIRTERCERO,
  COMPROBANTE_CNT.VLRAGIRAR,
  COMPROBANTE_CNT.DESCRIPCION,
  Comprobante_cnt.TEXTO,
  Comprobante_cnt.Nro_documento,
  CASE WHEN Detalle_comprobante_cnt.Auxiliar     = '99999999999999999999' THEN ' ' ELSE Detalle_comprobante_cnt.Auxiliar END AS CODAUXILIAR,
  CASE WHEN Detalle_comprobante_cnt.Centro_costo = '99999999999999999999' THEN ' ' ELSE Detalle_comprobante_cnt.Centro_costo END AS CODCENTRO,
  Comprobante_cnt.Vlr_documento AS VLR_DOCUMENTO,
  Detalle_comprobante_cnt.cuenta CODIGO_CUENTA,
  Tipo_comprobante.NOMBRE as NOMBRE_TIPO_COMPROBANTE,
  Trim(plan_contable.Nombre)
  || ' '
  ||
  CASE
    WHEN Tercero.NIT IS NULL
    THEN ' '
    ELSE ' / '
      || NVL(Tercero.Nombre,' ')
  END
  || ' '
  ||
  CASE
    WHEN Detalle_comprobante_cnt.Auxiliar = '99999999999999999999'
    OR Detalle_comprobante_cnt.Auxiliar  IS NULL
    THEN ' '
    ELSE ' / '
      || NVL(Auxiliar.Nombre,' ')
  END AS TOTALNOMBRE,
  CASE
    WHEN Detalle_comprobante_cnt.Centro_costo = '99999999999999999999'
    OR Detalle_comprobante_cnt.Centro_costo  IS NULL
    THEN ' '
    ELSE Detalle_comprobante_cnt.Centro_costo
      || ' / '
      || Centro_costo.Nombre
  END AS CENTRO,
  Detalle_comprobante_cnt.Valor_debito,
  Detalle_comprobante_cnt.Valor_credito,
CASE WHEN Detalle_comprobante_cnt.VALOR_DEBITO <>0 THEN 'a ' ||   Detalle_comprobante_cnt.cuenta ELSE 'z' || Detalle_comprobante_cnt.cuenta END AS Sec
FROM Comprobante_cnt
INNER JOIN Tercero Tercero_1
ON Tercero_1.COMPANIA  = Comprobante_cnt.COMPANIA
AND Tercero_1.NIT      = Comprobante_cnt.TERCERO
AND Tercero_1.SUCURSAL = Comprobante_cnt.SUCURSAL
INNER JOIN Detalle_comprobante_cnt
ON Comprobante_cnt.COMPANIA = Detalle_comprobante_cnt.COMPANIA
AND Comprobante_cnt.ANO     = Detalle_comprobante_cnt.ANO
AND Comprobante_cnt.TIPO    = Detalle_comprobante_cnt.TIPO_CPTE
AND Comprobante_cnt.NUMERO  = Detalle_comprobante_cnt.COMPROBANTE
INNER JOIN Centro_costo
ON Centro_costo.COMPANIA = Detalle_comprobante_cnt.COMPANIA
AND Centro_costo.ANO     = Detalle_comprobante_cnt.ANO
AND Centro_costo.CODIGO  = Detalle_comprobante_cnt.CENTRO_COSTO
LEFT JOIN Plan_contable
ON Detalle_comprobante_cnt.COMPANIA = Plan_contable.COMPANIA
AND Detalle_comprobante_cnt.ANO     = Plan_contable.ANO
AND Detalle_comprobante_cnt.CUENTA  = Plan_contable.CODIGO
LEFT JOIN Auxiliar
ON Detalle_comprobante_cnt.COMPANIA  = Auxiliar.COMPANIA
AND Detalle_comprobante_cnt.ANO      = Auxiliar.ANO
AND Detalle_comprobante_cnt.AUXILIAR = Auxiliar.CODIGO
LEFT JOIN Tercero
ON Detalle_comprobante_cnt.COMPANIA  = Tercero.COMPANIA
AND Detalle_comprobante_cnt.TERCERO  = Te]') || TO_CLOB(q'[rcero.NIT
AND Detalle_comprobante_cnt.SUCURSAL = Tercero.SUCURSAL
INNER JOIN Tipo_comprobante
ON Tipo_comprobante.COMPANIA = Comprobante_cnt.COMPANIA
AND Tipo_comprobante.CODIGO = Comprobante_cnt.TIPO
WHERE Comprobante_cnt.COMPANIA       =s$compania$s
AND Comprobante_cnt.NUMERO BETWEEN s$numeroPptoInicial$s AND s$numeroPptoFinal$s
AND Comprobante_cnt.TIPO='s$tipoCpte$s'
AND Comprobante_cnt.ANO =s$ano$s
ORDER BY Detalle_comprobante_cnt.COMPANIA,
  Detalle_comprobante_cnt.ANO,
  Detalle_comprobante_cnt.TIPO_CPTE,
  Detalle_comprobante_cnt.COMPROBANTE,
  Detalle_comprobante_cnt.CONSECUTIVO]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);
  
  MERGE INTO CONSULTAS_SUB FIN USING (SELECT '001523COMUPC' INFORME_PADRE, '000731COMRetenciones' INFORME_HIJO, 'PR_STRSQL_COMRETENCIONES' PARAMETRO ,NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (FIN.INFORME_PADRE = INI.INFORME_PADRE AND INI.INFORME_HIJO = FIN.INFORME_HIJO)  WHEN MATCHED THEN  UPDATE SET FIN.PARAMETRO = INI.PARAMETRO , FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME_PADRE,INFORME_HIJO, PARAMETRO,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME_PADRE,INI.INFORME_HIJO, INI.PARAMETRO,INI.CREATED_BY,SYSDATE);
  MERGE INTO CONSULTAS_SUB FIN USING (SELECT '001523COMUPC' INFORME_PADRE, '001512SubCOMUPC' INFORME_HIJO, 'PR_STRSQL_INFORMESUBCOM_DC' PARAMETRO ,NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (FIN.INFORME_PADRE = INI.INFORME_PADRE AND INI.INFORME_HIJO = FIN.INFORME_HIJO)  WHEN MATCHED THEN  UPDATE SET FIN.PARAMETRO = INI.PARAMETRO , FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME_PADRE,INFORME_HIJO, PARAMETRO,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME_PADRE,INI.INFORME_HIJO, INI.PARAMETRO,INI.CREATED_BY,SYSDATE);
