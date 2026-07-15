MERGE INTO CONSULTAS FIN USING (SELECT '800676F07aRelaciondePagosConsolidada' INFORME ,TO_CLOB(q'[SELECT 
     FECHA_PAGO                                                 " Fecha De Pago",
     COD_PPTAL                                               "Código Presupuestal",
     CLASE_PAGO                                               "Clase De Pago",
     TIPO_PAGO                                               "Tipo De Pago",
     NOCOMPROBANTE                                          "No De Comprobante",
     BENEFICIARIO                                            "Beneficiario",
     NIT                                                     "Cédula O Nit",
     DETALLE                                                "Detalle De Pago",
    VALOR                                                    Valor_comprobante, 
    DTO_SEGSOCIAL                                           Descuento_SegSocial,
    DTO_RETENCIONES                                         "Descuentos Retenciones",
    OTROS_DTOS                                              "Otros Descuentos",
    NETO_PAGADO                                             " Neto Pagado",
    BANCO                                                   "Banco",   
    NO_CUENTA                                                " No De Cuenta" ,
    NO_CHEQUE	                                             "No De Cheque O Nd" 
FROM SIA_RELACION_PAGOS
WHERE        ANO=s$ano$s
      AND MES_INICIAL>= s$mesInicial$s
      AND MES_FINAL <=  s$mesFinal$s]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);