MERGE INTO CONSULTAS_GENERALES FIN USING (SELECT '1000001' ID , TO_CLOB(q'[SELECT d.compania, d.placa, d.elemento codelemento, inv.nombrelargo nombreelemento,
  d.dependencia coddependencia, dp.nombre nombredependencia, d.responsable codresponsable, r.cedula cedularesponsable,
  t.nombre nombreresponsable, ed.descripcion estado,d.valor,
  REPLACE(d.descripcion,'00:00:00','00-00-00') descripcion,d.serie,
to_date(TO_CHAR(d.fechaadquisicion, 'DD/MM/YYYY')) fechaadquisicion,
to_date(TO_CHAR(sysdate, 'DD/MM/YYYY')) fechaCarga,
d.MARCA,
d.SERIEDEVOLUTIVO,
d.MODELO
FROM devolutivo d INNER JOIN dependencia dp
  ON d.compania = dp.compania
  AND d.dependencia = dp.codigo
INNER JOIN responsable r
  ON d.compania = r.compania
  AND d.responsable = r.cedula
  AND sucursal_responsable = r.sucursal
INNER JOIN estadodevolutivo ed
  ON d.estado = ed.estado
INNER JOIN inventario inv
  ON d.compania = inv.compania
  AND d.elemento = inv.codigoelemento
INNER JOIN tercero t
  ON r.compania = t.compania
  AND r.cedula = t.nit
  AND r.sucursal = t.sucursal  
WHERE d.compania = :UN_COMPANIA
AND d.dependencia BETWEEN :UN_DEPENDENCIAINI AND :UN_DEPENDENCIAFIN
AND dp.movimiento <> 0
]') SQL, '' COLUMNAS_LLAVE , TO_CLOB(q'[]') PAR_ENTRADA, TO_CLOB(q'[]') PAR_ENTRADA_DEFAULT, TO_CLOB(q'[]') PAR_SALIDA, 'mzanguna' CREATED_BY, NULL MODIFIED_BY, 0 BORRADO_LOGICO,  '' USECONFIG, 0 PLANTILLA  FROM DUAL ) INI ON (INI.ID = FIN.ID)  WHEN MATCHED THEN  UPDATE SET FIN.CREATED_BY = INI.CREATED_BY, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.SQL = INI.SQL, FIN.PAR_SALIDA = INI.PAR_SALIDA, FIN.BORRADO_LOGICO = INI.BORRADO_LOGICO, FIN.USECONFIG = INI.USECONFIG, FIN.COLUMNAS_LLAVE = INI.COLUMNAS_LLAVE, FIN.PAR_ENTRADA = INI.PAR_ENTRADA, FIN.PAR_ENTRADA_DEFAULT = INI.PAR_ENTRADA_DEFAULT, FIN.PLANTILLA = INI.PLANTILLA  WHEN NOT MATCHED THEN  INSERT (CREATED_BY, DATE_CREATED, MODIFIED_BY, SQL, ID, PAR_SALIDA, BORRADO_LOGICO, USECONFIG, COLUMNAS_LLAVE, PAR_ENTRADA, PAR_ENTRADA_DEFAULT, PLANTILLA)  VALUES (INI.CREATED_BY, SYSDATE, INI.MODIFIED_BY, INI.SQL, INI.ID, INI.PAR_SALIDA, INI.BORRADO_LOGICO, INI.USECONFIG, INI.COLUMNAS_LLAVE, INI.PAR_ENTRADA, INI.PAR_ENTRADA_DEFAULT, INI.PLANTILLA);