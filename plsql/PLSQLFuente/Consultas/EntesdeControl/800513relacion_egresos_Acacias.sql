MERGE INTO CONSULTAS FIN USING (SELECT '800513relacion_egresos_Acacias' INFORME ,TO_CLOB(q'[SELECT DC.TIPO_CPTE Tipo_Egreso,
    DC.COMPROBANTE Numero_Egreso,
    TO_CHAR(DC.FECHA,'YYYY-MM-DD')Fecha_Pago,
    REPLACE(REPLACE(NVL(DC.DESCRIPCION,'N/A'), CHR(13), ' ') , CHR(10), ' ') Descripcion,
     NVL(DC.TERCERO,0)                                           Identificacion,
     NVL(T.NOMBRE,'N/A')                                         Tercero,
       NVL(EGRESOS_TESORERIA.NOMBREBANCO,'N/A')                    Banco,  
      NVL(EGRESOS_TESORERIA.CUENTANUMERO,'N/A')                   No_Cuenta ,
      NVL(REPLACE(SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO),'.',','),0)  Valor_Bruto,
       REPLACE(round(NVL(EGRESOS_TESORERIA.RETENCION,0)/COUNT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE,  DC.CUENTA ),2),'.',',') Valor_Deducciones,  
     REPLACE(SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)      
         - ROUND(NVL(EGRESOS_TESORERIA.RETENCION,0)/COUNT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2),'.',',')  Neto_Pagado,     
       TO_CHAR(DC.tIPO_CPTE_AFECT) Tipo_orden_Pago,
       NVL(TO_CHAR(DC.CMPTE_AFECTADO),0)Orden_de_Pago,  
       DC.FUENTE_RECURSO Cod_Fuente,
       REPLACE(REPLACE(NVL(FUENTE_RECURSOS.NOMBRE,'N/A'), CHR(13), ' ') , CHR(10), ' ') Fuente_Recurso,
	   DC.CUENTA Rubro_Presupuestal,
       REPLACE(REPLACE(NVL(TRIM(PP.NOMBRE),'N/A'), CHR(13), ' ') , CHR(10), ' ') Nombre_Rubro
  
FROM DETALLE_COMPROBANTE_PPTAL DC 
       INNER JOIN TIPO_COMPROBPP TC
             ON DC.COMPANIA=TC.COMPANIA
              AND DC.TIPO_CPTE=TC.CODIGO
        INNER JOIN COMPROBANTE_CNT CN
             ON DC.COMPANIA=CN.COMPANIA
              AND DC.ANO=CN.ANO
              AND DC.TIPO_CPTE=CN.TIPO
               AND DC.COMPROBANTE=CN.NUMERO
         INNER JOIN COMPANIA C 
         ON CN.COMPANIA= C.CODIGO    
       INNER JOIN PLAN_PRESUPUESTAL PP
          ON DC.COMPANIA=PP.COMPANIA
              AND DC.ANO=PP.ANO
              AND DC.CUENTA=PP.CODIGO
      INNER JOIN TERCERO T
           ON DC.COMPANIA=T.COMPANIA
           AND DC.TERCERO=T.NIT
           AND DC.SUCURSAL=T.SUCURSAL      
      LEFT JOIN FUENTE_RECURSOS
                 ON DC.COMPANIA=FUENTE_RECURSOS.COMPANIA
                   AND  DC.ANO=FUENTE_RECURSOS.ANO
                   AND CASE WHEN PP.MAN_AUX_FUE =-1 THEN DC.FUENTE_RECURSO ELSE PP.FUENTE_RECURSOS END=FUENTE_RECURSOS.CODIGO

LEFT JOIN (SELECT DISTINCT CB.COMPANIA,
                 CB.ANO,
                 CB.TIPO_CPTE,
                 CB.COMPROBANTE,
                 NVL(DESCUENTOS.SEGURIDAD_SOCIAL,0) SEGURIDAD_SOCIAL,
                 NVL(DESCUENTOS.RETENCION,0)  RETENCION,
                 NVL(DESCUENTOS.OTROS_DESCUENTOS,0) OTROS ,
                 CB.CODBANCO_SIA,
                 CB.BANCO,
                 BANCO.NOMBREBANCO,
                 CB.CUENTANUMERO,
                 CB.RECURSOS
          FROM       (SELECT UNICO.COMPANIA, 
                               UNICO.ANO, 
              ]') || TO_CLOB(q'[                 UNICO.TIPO_CPTE, 
                               UNICO.COMPROBANTE, 
                               UNICO.CUENTA,
                               CUENTABANCOS.CODBANCO_SIA,
                               CUENTABANCOS.BANCO,
                               CUENTABANCOS.CUENTANUMERO,
                               CUENTABANCOS.RECURSOS
                        FROM (SELECT DET.COMPANIA, 
                                     DET.ANO, 
                                     DET.TIPO_CPTE, 
                                     DET.COMPROBANTE,
                                    MIN(DET.CUENTA) CUENTA
                              FROM DETALLE_COMPROBANTE_CNT DET INNER JOIN CUENTABANCOS CUENTABANCOS
                                ON DET.COMPANIA       = CUENTABANCOS.COMPANIA
                               AND DET.ANO           = CUENTABANCOS.ANO
                               AND DET.CUENTA        = CUENTABANCOS.IDCONTABLE
                              INNER JOIN TIPO_COMPROBANTE TC
                                 ON DET.COMPANIA=TC.COMPANIA
                                 AND DET.TIPO_CPTE=TC.CODIGO
                              WHERE DET.COMPANIA=s$compania$s 
                                AND DET.ANO=s$ano$s  
                                AND DET.MES BETWEEN s$mesInicial$s AND s$mesFinal$s 
                                AND TC.CLASE_CONTABLE IN ('E','G','A')
                              GROUP BY DET.COMPANIA, 
                                     DET.ANO, 
                                     DET.TIPO_CPTE, 
                                     DET.COMPROBANTE 
                                     ) UNICO INNER JOIN CUENTABANCOS CUENTABANCOS
                          ON UNICO.COMPANIA = CUENTABANCOS.COMPANIA
                         AND UNICO.ANO      = CUENTABANCOS.ANO
                         AND UNICO.CUENTA   = CUENTABANCOS.IDCONTABLE

                         WHERE CUENTABANCOS.COMPANIA=s$compania$s 
                            AND CUENTABANCOS.ANO=s$ano$s  
                            AND CUENTABANCOS.ESTADO IN('A')
                 ) CB
             LEFT JOIN (SELECT COMPANIA,
                               ANO_AFECT ANO,
                               TIPO_CPTE_AFECT TIPO_CPTE,
                               COMPROBANTE_AFECT COMPROBANTE,
                                    SUM(RETENCION) RETENCION,
                               SUM(SEGURIDAD_SOCIAL) SEGURIDAD_SOCIAL,
                               SUM(OTROS_DESCUENTOS) OTROS_DESCUENTOS

                        FROM (
                                SELECT DC.COMPANIA,
                                       DC.ANO,
                                       DC.TIPO_CPTE,
                                       DC.COMPROBANTE,
                                       DC.CONSECUTIVO,
                                       EGR.aNO ANO_AFECT,
                                       EGR.TIPO_cPte TIPO_CPTE_AFECT,
               ]') || TO_CLOB(q'[                        EGR.cOmpRoBaNTE COMPROBANTE_AFECT,
                                       CASE WHEN P.TIPODESCUENTO_SIA='RETENCION'
                                            THEN DC.VALOR_CREDITO
                                            ELSE 0 END/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE, DC.CONSECUTIVO ) RETENCION,
                                       CASE WHEN P.TIPODESCUENTO_SIA='SEGURIDAD_SOCIAL'
                                            THEN DC.VALOR_CREDITO
                                            ELSE 0 END/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE, DC.CONSECUTIVO ) SEGURIDAD_SOCIAL,
                                       CASE WHEN P.TIPODESCUENTO_SIA='OTRO'
                                            THEN DC.VALOR_CREDITO
                                            ELSE 0 END/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE, DC.CONSECUTIVO ) OTROS_DESCUENTOS
                                FROM DETALLE_COMPROBANTE_CNT DC 
                                          INNER JOIN TIPO_COMPROBANTE TC
                                               ON DC.COMPANIA=TC.COMPANIA
                                               AND DC.TIPO_CPTE=TC.CODIGO                           
                                          INNER JOIN  COMPROBANTE_CNTAFECTADOS EGR 
                                               ON DC.COMPANIA=EGR.COMPANIA
                                               AND DC.ANO =EGR.ANO_AFECT
                                               AND DC.TIPO_CPTE=EGR.TIPO_CPTE_AFECT
                                               AND DC.COMPROBANTE=EGR.COMPROBANTE_AFECT                           
                                          INNER JOIN PLAN_CONTABLE P
                                               ON DC.COMPANIA=P.COMPANIA
                                               AND DC.CUENTA=P.CODIGO
                                               AND DC.ANO=P.ANO  
                                        WHERE DC.COMPANIA=s$compania$s 
                                             AND DC.ANO=s$ano$s  
                                             AND DC.MES BETWEEN s$mesInicial$s AND s$mesFinal$s  
                                             AND P.TIPODESCUENTO_SIA IS NOT NULL
                                             AND TC.CLASE_CONTABLE IN ('P')                         
                        ) 
                        GROUP BY COMPANIA,
                               ANO_AFECT ,
                               TIPO_CPTE_AFECT ,
                               COMPROBANTE_AFECT


             ) DESCUENTOS       
                    ON CB.COMPANIA=DESCUENTOS.COMPANIA
                    AND CB.ANO=DESCUENTOS.ANO
                    AND CB.TIPO_CPTE=DESCUENTOS.TIPO_CPTE
                    AND CB.COMPROBANTE=DESCUENTOS.COMPROBANTE
               LEFT JO]') || TO_CLOB(q'[IN BANCO
                        ON CB.COMPANIA=BANCO.COMPANIA
                        AND CB.BANCO=BANCO.BANCO

                  WHERE CB.COMPANIA=s$compania$s 
                        AND CB.ANO=s$ano$s  

    ) EGRESOS_TESORERIA
     ON DC.COMPANIA   =EGRESOS_TESORERIA.COMPANIA
   AND DC.ANO        =EGRESOS_TESORERIA.ANO
   AND DC.TIPO_CPTE =EGRESOS_TESORERIA.TIPO_CPTE
   AND DC.COMPROBANTE =EGRESOS_TESORERIA.COMPROBANTE
 INNER JOIN COMPROBANTE_PPTAL CP   
 ON DC.COMPANIA=CP.COMPANIA
 AND DC.ANO_AFECT=CP.ANO
 AND DC.TIPO_CPTE_AFECT=CP.TIPO
 AND DC.CMPTE_AFECTADO=CP.NUMERO

  WHERE DC.COMPANIA=s$compania$s 
      AND DC.ANO=s$ano$s  
      AND DC.MES BETWEEN s$mesInicial$s AND s$mesFinal$s 
      AND TC.CLASE IN ('EGR','DEG','AEG')
      AND PP.REGALIAS IN(0)
GROUP BY  
 DC.TIPO_CPTE, DC.COMPROBANTE,  DC.COMPANIA,DC.ANO,
      CASE WHEN  pp.TIPOVIGENCIA ='RA'
          THEN 'Reservas'
          ELSE CASE WHEN pp.TIPOVIGENCIA ='RC'
          THEN 'CxP'
             ELSE 'Actual' END END,
       SUBSTR(REPLACE(C.NITCOMPANIA, '.', ''),1,9),
       c.NOMBRE,
       DC.CUENTA,
       FUENTE_RECURSOS.NOMBRE,
	   TRIM(PP.NOMBRE),
       DC.FUENTE_RECURSO ,
	TO_CHAR(DC.tIPO_CPTE_AFECT),
    DC.COMPROBANTE,
        TO_CHAR(DC.FECHA,'YYYY-MM-DD'),
       DC.CMPTE_AFECTADO ,
       TO_CHAR(CP.FECHA,'YYYY-MM-DD'),
      EGRESOS_TESORERIA.NOMBREBANCO      ,  
      EGRESOS_TESORERIA.CUENTANUMERO  ,
      DC.TERCERO ,                                  
      T.NOMBRE   ,
      DC.DESCRIPCION
      ,NVL(EGRESOS_TESORERIA.RETENCION,0)]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);