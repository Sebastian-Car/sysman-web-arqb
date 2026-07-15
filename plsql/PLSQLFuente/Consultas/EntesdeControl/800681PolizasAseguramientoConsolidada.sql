MERGE INTO CONSULTAS FIN USING (SELECT '800681PolizasAseguramientoConsolidada' INFORME ,TO_CLOB(q'[SELECT ENT_ASEG                        Entidad_Aseguradora,
       POLIZA                          POLIZA,
       VIGENCIA_INI                    Vigencia_inicial,          
       VIGENCIA_FIN                    Vigencia_Final,
       RIESGO_ASEG                    Riesgo_adsegurado,
       TOMADOR                        Tomador,
       DEPENDENCIA                     Dependencia,
       CARGO                          Cargo,
      ASEGURADO                      Asegurado,
      TIPO_AMPARO                   Tipo_Amparo,
      VR_ASEGURADO                  Valor_Asegurado
FROM SIA_POLIZAS_ASEGURAMIENTO
WHERE        ANO=s$ano$s
      AND MES_INICIAL>=s$mesInicial$s
      AND MES_FINAL <=s$mesFinal$s]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);