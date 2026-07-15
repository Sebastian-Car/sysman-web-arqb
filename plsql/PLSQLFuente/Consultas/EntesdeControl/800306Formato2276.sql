MERGE INTO CONSULTAS FIN USING (SELECT '800306Formato2276' INFORME ,TO_CLOB(q'[SELECT '1'  									ENTIDAD_INFORMANTE,
	   TIPODCTO                             TIPO_DOCUMENTO, 
       NUMERO_DOCUMENTO                     NUMERO_DE_IDENTIFICACION,
       APELLIDO1                            PRIMER_APELLIDO,
       APELLIDO2                            SEGUNDO_APELLIDO, 
       NOMBRE1                              PRIMER_NOMBRE,
       NOMBRE2                              OTROS_NOMBRES,
       DIRECCION                            DIRECCION,
       CODIGODEPARTAMENTO                   DEPARTAMENTO_BENEFICIARIO,
       CODIGOCIUDAD                         MUNICIPIO_BENEFICIARIO,
       '169'                                PAIS,
       S31                                  PAGOS_POR_SALARIOS,
       0                                    PAGOS_EMOLUMENTOS,   
       0                                    PAGOS_REALIZADOS_BONOS,
       0  									PAGOS_AUVT,
       S43                                  PAGOS_HONORARIOS,
       S44                                  PAGOS_SERVICOS,
       S45                                  PAGOS_COMISIONES,
       S46                                  PAGOS_PRESTACIONES_SOCIALES, 
       S47                                  PAGOS_VIATICOS,
       S33                                  PAGOS_GASTOS_REPRESENTACION,
       S48                                  PAGOS_POR_COMPENSACIONES,
       0  									APOYOS_ECOFPE,
       S35                                  OTROS_PAGOS,      
       CASE WHEN REGIMEN = 2 THEN
       	S32                                  
       ELSE
       0 END 								CESANTIAS_INTERESES_CESANTIAS,
       S51									CESANTIAS_CONSIGNADAS,
       CASE WHEN REGIMEN = 1 THEN
       	S32                                  
       ELSE
       0 END 								CESANTIAS_RETROACTIVO,
       S34                                  PENSIONES_JUBILIACION_VEJEZ,
       ROUND(NVL(S31,0),-3)+ROUND(NVL(S43,0),-3)+ROUND(NVL(S44,0),-3)+ROUND(NVL(S45,0),-3)+ROUND(NVL(S46,0),-3)+ROUND(NVL(S33,0),-3)+ROUND(NVL(S48,0),-3)+ROUND(NVL(S35,0),-3)+ROUND(NVL(S32,0),-3)+ROUND(NVL(S47,0),-3)+ROUND(NVL(S34,0),-3)+ROUND(NVL(S51,0),-3) AS TOTAL_INGRESOBTP,
       S37                                  APORTES_OBLIGATORIOS_SALUD,
       S38                                  APORTES_OBLIGATORIOS_PENSION,
       S36									APORTES_VOLUNTARIOS_RAIS,
       S41                                  APORTES_VOLUNTARIOS_PENSION,
       S42                                  APORTES_CUENTAS_AFC,
       0 									APORTES_CUENTAS_AVC,
       RETE                                 VALOR_RETENCIONES,
       0 									VALOR_IVAMC,
       0 									VALOR_RETEIVA,
       0 									VALOR_PAGOS_ALIMENTACION,
       PROM6 									VALOR_INGRESO_LABPROM,
       '' 									TIPO_DOCUMENTO_DE,
       0 									NUMERO_IDENTIFICACION_DE,
       0 									IDENTIFICACION_FIDECOMISO,
       0 									TIPO_DOCUMENTO_PCC,
       0 									NUMERO_IDENTIFICACION_PCC
FROM V_DIAN
WHERE COMPANIA=s$compania$s
AN]') || TO_CLOB(q'[D ANO=s$ano$s
AND(S31+S32+S33+S34+S35++S37+S38+S41+S42+S43+S44+S45+S46+S47+S48)>0]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, 'MROSERO' MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);
