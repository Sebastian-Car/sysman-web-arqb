MERGE INTO CONSULTAS FIN USING (SELECT '800168InformeF07RelacionDePagosGobNar' INFORME ,TO_CLOB(q'[SELECT  TO_CHAR(DC.FECHA,'DD/MM/YYYY')                             FECHA_DE_PAGO,
       PCK_SYSMAN_UTL.FC_NOMBRE_MES(s$mesFinal$s)                 MESREPORTADO,
       PPC.CODIGO_SIA||DC.CUENTA                                  CODIGO_PRESUPUESTAL,
       CN.TIPOPAGO_SIA                                            TIPO_DE_PAGO,
      EGRESOS_TESORERIA.RECURSOS                                   FUENTE_DE_FINANCIACION,
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE                             NO_COMPROBANTE,
       T.NOMBRE                                                   BENEFICIARIO,
       DC.TERCERO                                                 CEDULA_NIT,
       DC.DESCRIPCION                                             DETALLE_DE_PAGO,
       SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)                                       VALOR_COMPROBANTE_DE_PAGO, 
       ROUND(NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2) DESCUENTO_SEG_SOCIAL,
      ROUND(NVL(EGRESOS_TESORERIA.RETENCION,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE),2) DESCUENTO_RETENCION,
      ROUND(NVL(EGRESOS_TESORERIA.OTROS,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2) OTROS_DESCUENTOS,
     SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO) 
      - ROUND(NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2)
      - ROUND(NVL(EGRESOS_TESORERIA.RETENCION,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2)
      - ROUND(NVL(EGRESOS_TESORERIA.OTROS,0)/COUnT(DC.COMPANIA) OVER (Partition BY DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE ),2)
      NETO_PAGO,
      EGRESOS_TESORERIA.CODBANCO_SIA                              BANCO,  
      EGRESOS_TESORERIA.CUENTANUMERO                              NUMERO_CUENTA ,
      NVL(CN.NRO_DOCUMENTO,'ND')                             NUMERO_DE_CHEQUE
FROM DETALLE_COMPROBANTE_PPTAL DC 
       INNER JOIN TIPO_COMPROBPP TC
             ON DC.COMPANIA=TC.COMPANIA
              AND DC.TIPO_CPTE=TC.CODIGO
        INNER JOIN COMPROBANTE_CNT CN
             ON DC.COMPANIA=CN.COMPANIA
              AND DC.ANO=CN.ANO
              AND DC.TIPO_CPTE=CN.TIPO
               AND DC.COMPROBANTE=CN.NUMERO
     INNER JOIN PLAN_PRESUPUESTAL PP
          ON DC.COMPANIA=PP.COMPANIA
              AND DC.ANO=PP.ANO
              AND DC.CUENTA=PP.CODIGO
      INNER JOIN TERCERO T
           ON DC.COMPANIA=T.COMPANIA
           AND DC.TERCERO=T.NIT
           AND DC.SUCURSAL=T.SUCURSAL
    LEFT JOIN PLAN_PPTAL_CONFIG PPC 
    ON  DC.COMPANIA=PPC.COMPANIA
    AND DC.ANO=PPC.ANO
    AND DC.ANO=PPC.ANO
    AND DC.CUENTA=PPC.CODIGO
    AND DC.CENTRO_COSTO=PPC.CENTRO_COSTO
    AND DC.AUXILIAR=PPC.AUXILIAR
    AND DC.FUENTE_RECURSO=PPC.FUENTE_RECURSO      
LEFT JOIN (SELECT DISTINCT CB.COMPANIA,
 ]') || TO_CLOB(q'[                CB.ANO,
                 CB.TIPO_CPTE,
                 CB.COMPROBANTE,
                 NVL(DESCUENTOS.SEGURIDAD_SOCIAL,0) SEGURIDAD_SOCIAL,
                 NVL(DESCUENTOS.RETENCION,0)  RETENCION,
                 NVL(DESCUENTOS.OTROS_DESCUENTOS,0) OTROS ,
                 CB.CODBANCO_SIA,
                 CB.CUENTANUMERO,
                 CB.NRO_DOCUMENTO,
                 CB.RECURSOS
          FROM       (SELECT UNICO.COMPANIA, 
                               UNICO.ANO, 
                               UNICO.TIPO_CPTE, 
                               UNICO.COMPROBANTE, 
                               UNICO.CUENTA,
                               CUENTABANCOS.CODBANCO_SIA,
                               CUENTABANCOS.CUENTANUMERO,
                               UNICO.NRO_DOCUMENTO,
                               CUENTABANCOS.RECURSOS
                        FROM (SELECT DET.COMPANIA, 
                                     DET.ANO, 
                                     DET.TIPO_CPTE, 
                                     DET.COMPROBANTE,
                                     CNT.NRO_DOCUMENTO,
                                     MIN(DET.CUENTA) CUENTA
                              FROM DETALLE_COMPROBANTE_CNT DET INNER JOIN CUENTABANCOS CUENTABANCOS
                                ON DET.COMPANIA       = CUENTABANCOS.COMPANIA
                               AND DET.ANO           = CUENTABANCOS.ANO
                               AND DET.CUENTA        = CUENTABANCOS.IDCONTABLE
                               INNER JOIN COMPROBANTE_CNT CNT
                               ON DET.COMPANIA=CNT.COMPANIA
                               AND DET.ANO=CNT.ANO
                               AND DET.TIPO_CPTE=CNT.TIPO
                                AND DET.COMPROBANTE=CNT.NUMERO
                              INNER JOIN TIPO_COMPROBANTE TC
                                 ON DET.COMPANIA=TC.COMPANIA
                                 AND DET.TIPO_CPTE=TC.CODIGO
                              WHERE DET.COMPANIA=s$compania$s
                                AND DET.ANO=s$ano$s
                                AND DET.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
                                AND TC.CLASE_CONTABLE IN ('E','G','A')
                              GROUP BY DET.COMPANIA, 
                                     DET.ANO, 
                                     DET.TIPO_CPTE, 
                                     DET.COMPROBANTE ,
                                     CNT.NRO_DOCUMENTO
                           ) UNICO INNER JOIN CUENTABANCOS CUENTABANCOS
                          ON UNICO.COMPANIA = CUENTABANCOS.COMPANIA
                         AND UNICO.ANO      = CUENTABANCOS.ANO
                         AND UNICO.CUENTA   = CUENTABANCOS.IDCONTABLE
                         WHERE CUENTABANCOS.COMPANIA=s$compania$s
                            AND CUENTABANCOS.ANO=s$ano$s
                            AND CUENTABANCOS.ESTADO IN('A')
                 ) C]') || TO_CLOB(q'[B
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
                                       EGR.cOmpRoBaNTE COMPROBANTE_AFECT,
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
                                ]') || TO_CLOB(q'[            -- AND DC.ANO=s$ano$s
                                           -- AND DC.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
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
                  WHERE CB.COMPANIA=s$compania$s
                        AND CB.ANO=s$ano$s

    ) EGRESOS_TESORERIA
     ON DC.COMPANIA   =EGRESOS_TESORERIA.COMPANIA
   AND DC.ANO        =EGRESOS_TESORERIA.ANO
   AND DC.TIPO_CPTE =EGRESOS_TESORERIA.TIPO_CPTE
   AND DC.COMPROBANTE =EGRESOS_TESORERIA.COMPROBANTE

  WHERE DC.COMPANIA=s$compania$s
      AND DC.ANO=s$ano$s
      AND DC.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
      AND TC.CLASE IN ('EGR','DEG')
      AND PP.TIPOVIGENCIA  NOT IN ('RC')
      AND PP.REGALIAS IN(0)
GROUP BY DC.FECHA,                       
        PPC.CODIGO_SIA||DC.CUENTA,  
       PP.NOMBRE,                                                  
       T.NOMBRE,                                                  
       DC.TERCERO,  
       CN.TIPOPAGO_SIA,
       EGRESOS_TESORERIA.RECURSOS ,
       DC.DESCRIPCION,
      NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0),              
      NVL(EGRESOS_TESORERIA.RETENCION,0), 
      NVL(EGRESOS_TESORERIA.OTROS,0),
      EGRESOS_TESORERIA.CODBANCO_SIA,                         
      EGRESOS_TESORERIA.CUENTANUMERO,                            
      CN.NRO_DOCUMENTO,
     PCK_SYSMAN_UTL.FC_NOMBRE_MES(s$mesFinal$s),     
     DC.compania, DC.ano, DC.TIPO_CPTE, DC.COMPROBANTE, DC.CUENTA]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);