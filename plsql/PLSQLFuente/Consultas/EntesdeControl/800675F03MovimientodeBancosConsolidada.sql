MERGE INTO CONSULTAS FIN USING (SELECT '800675F03MovimientodeBancosConsolidada' INFORME ,TO_CLOB(q'[SELECT
       BANCO        " Banco",
       NO_CUENTA                               "Numero Cuenta",
       MAX(DENOMINACION)                                   "Denominación",
       FUENTE_FIN                                     "Fuente De Financiación",
       SUM(SALDO_INI_01ENERO)                     " Saldo Inicial A 1 De Enero",
       SUM(INGRESOS)           "|D| Ingresos",
       SUM(EGRESOS)                   "Egresos",
       SUM(NOTAS_DEB)              " Notas Debito",
       SUM(NOTAS_CRED)             "Notas Credito",
       SUM(SALDO_31DIC_LIBROS)                        "Saldo Libros",
       SUM(SALDO_31DIC_EXTRACTO)                        "Saldo Extractos Bancarios"
FROM SIA_MOVIMIENTO_BANCOS
  WHERE ANO = s$ano$s
 AND MES_FINAL <= s$mesFinal$s
 GROUP BY  BANCO,NO_CUENTA,FUENTE_FIN  ]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);