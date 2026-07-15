MERGE INTO CONSULTAS FIN USING (SELECT '800674EjecucionPresupuestaldeIngresosConsolidada' INFORME ,TO_CLOB(q'[SELECT
         RUBRO                                              "Cod Rubro Presupuestal",
         MAX(NOMBRE)                                       "Nombre Rubro Presupuestal",
        SUM(PPTO_INICIAL)                                  "Apropiación Inicial",
        SUM(ADICIONES)                                      " Adiciones",
        SUM(REDUCCIONES)                                    "Reducciones",
       SUM(RECAUDOS)                                        "Recaudos"
       
FROM SIA_EJECUCION_PRESUPUESTAL_INGRESOS
  WHERE ANO      = s$ano$s
  AND MES_FINAL <= s$mesFinal$s
  GROUP BY  RUBRO ]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);