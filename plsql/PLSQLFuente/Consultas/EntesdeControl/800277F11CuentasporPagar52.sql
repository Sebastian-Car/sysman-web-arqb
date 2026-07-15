SELECT PPC.CODIGO_SIA||DC.CUENTA                                  CODIGO_PRESUPUESTAL,
       PP.NOMBRE                                                  DESCRIPCION_RUBRO,
       SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)                      CXP_CONSTITUIDA, 
       TO_CHAR(DC.FECHA,'DD/MM/YYYY')                             FECHA_DE_PAGO,
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE                          NO_COMPROBANTE,
       T.NOMBRE                                                   BENEFICIARIO,
       DC.TERCERO                                                 CEDULA_NIT,
       DC.DESCRIPCION                                             DETALLE_DE_PAGO,
      SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)                       VALOR_COMPROBANTE_DE_PAGO,
      NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0)                   DESCUENTO_SEG_SOCIAL,
      NVL(EGRESOS_TESORERIA.RETENCION,0)                          DESCUENTO_RETENCION,
      NVL(EGRESOS_TESORERIA.OTROS,0)                              OTROS_DESCUENTOS,
      SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)                       NETO_PAGO,
      EGRESOS_TESORERIA.CODBANCO_SIA                              BANCO,  
      EGRESOS_TESORERIA.CUENTANUMERO                              NUMERO_CUENTA ,
     NVL( EGRESOS_TESORERIA.NRO_DOCUMENTO,'ND')                   NUMERO_DE_CHEQUE
   
FROM DETALLE_COMPROBANTE_PPTAL DC 
      LEFT JOIN  COMPROBANTE_CNT CN
              ON DC.COMPANIA=CN.COMPANIA
              AND DC.ANO=CN.ANO
              AND DC.TIPO_CPTE=CN.TIPO
              AND DC.COMPROBANTE=CN.NUMERO
       INNER JOIN TIPO_COMPROBPP TC
             ON DC.COMPANIA=TC.COMPANIA
              AND DC.TIPO_CPTE=TC.CODIGO
       INNER JOIN FUENTE_RECURSOS FR
             ON DC.COMPANIA=FR.COMPANIA
             AND DC.ANO=FR.ANO
             AND DC.FUENTE_RECURSO=FR.CODIGO
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
LEFT JOIN (SELECT DISTINCT DC.COMPANIA,
                 DC.ANO,
                 DC.TIPO_CPTE,
                 DC.COMPROBANTE,
                 NVL(DESCUENTOS.SEGURIDAD_SOCIAL,0) SEGURIDAD_SOCIAL,
                 NVL(DESCUENTOS.RETENCION,0)  RETENCION,
                 NVL(DESCUENTOS.OTROS_DESCUENTOS,0) OTROS ,
                 CB.CODBANCO_SIA,
                 CB.CUENTANUMERO,
                 DC.NRO_DOCUMENTO
          FROM  V_DETALLE_AUXILIAR_CNT DC 
            INNER JOIN COMPROBANTE_PPTAL  CP
              ON DC.COMPANIA=CP.COMPANIA
              AND DC.ANO=CP.ANO
              AND DC.TIPO_CPTE=CP.TIPO
              AND DC.COMPROBANTE=CP.NUMERO
            INNER JOIN (SELECT UNICO.COMPANIA, 
       UNICO.ANO, 
       UNICO.TIPO_CPTE, 
       UNICO.COMPROBANTE, 
       UNICO.CUENTA,
       CUENTABANCOS.CODBANCO_SIA,
       CUENTABANCOS.CUENTANUMERO
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
        AND DET.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
        AND TC.CLASE_CONTABLE IN ('E','G','A')
      GROUP BY DET.COMPANIA, 
             DET.ANO, 
             DET.TIPO_CPTE, 
             DET.COMPROBANTE  
   ) UNICO INNER JOIN CUENTABANCOS CUENTABANCOS
  ON UNICO.COMPANIA = CUENTABANCOS.COMPANIA
 AND UNICO.ANO      = CUENTABANCOS.ANO
 AND UNICO.CUENTA   = CUENTABANCOS.IDCONTABLE
 WHERE ESTADO IN('A')) CB
               ON DC.COMPANIA=CB.COMPANIA
               AND DC.ANO=CB.ANO
               AND DC.CODIGO_CUENTA=CB.CUENTA
  AND DC.TIPO_CPTE=CB.TIPO_CPTE
  AND DC.COMPROBANTE=CB.COMPROBANTE
            
             LEFT JOIN (SELECT DC.COMPANIA,
                               DC.ANO,
                               DC.TIPO_CPTE,
                               DC.COMPROBANTE,
                              SUM(CASE WHEN P.TIPODESCUENTO_SIA='RETENCION'
                                    THEN VALOR_CREDITO
                                    ELSE 0 END) RETENCION,
                              SUM(CASE WHEN P.TIPODESCUENTO_SIA='SEGURIDAD_SOCIAL'
                                    THEN VALOR_CREDITO
                                    ELSE 0 END) SEGURIDAD_SOCIAL,
                              SUM(CASE WHEN P.TIPODESCUENTO_SIA='OTRO'
                                    THEN VALOR_CREDITO
                                    ELSE 0 END) OTROS_DESCUENTOS
                       FROM DETALLE_COMPROBANTE_CNT DC 
                                  INNER JOIN PLAN_CONTABLE P
                                       ON DC.COMPANIA=P.COMPANIA
                                       AND DC.CUENTA=P.CODIGO
                                       AND DC.ANO=P.ANO
                                  INNER JOIN TIPO_COMPROBANTE TC
                                       ON DC.COMPANIA=TC.COMPANIA
                                       AND DC.TIPO_CPTE=TC.CODIGO
                                WHERE DC.COMPANIA=s$compania$s
                                     AND DC.ANO=s$ano$s
                                     AND DC.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
                                     AND P.TIPODESCUENTO_SIA IS NOT NULL
                                     AND TC.CLASE_CONTABLE IN ('E','G','A')
                               GROUP BY DC.COMPANIA,
                                        DC.ANO,
                                        DC.TIPO_CPTE,
                                        DC.COMPROBANTE) DESCUENTOS       
                    ON DC.COMPANIA=DESCUENTOS.COMPANIA
                    AND DC.ANO=DESCUENTOS.ANO
                    AND DC.TIPO_CPTE=DESCUENTOS.TIPO_CPTE
                    AND DC.COMPROBANTE=DESCUENTOS.COMPROBANTE
                  WHERE DC.COMPANIA=s$compania$s
                        AND DC.ANO=s$ano$s
                        AND DC.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s) EGRESOS_TESORERIA
             ON DC.COMPANIA=EGRESOS_TESORERIA.COMPANIA
   AND DC.ANO=EGRESOS_TESORERIA.ANO
   AND DC.TIPO_CPTE=EGRESOS_TESORERIA.TIPO_CPTE
   AND DC.COMPROBANTE=EGRESOS_TESORERIA.COMPROBANTE
   
  WHERE DC.COMPANIA=s$compania$s
      AND DC.ANO=s$ano$s
      AND DC.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
      AND TC.CLASE IN ('EGR','DEG')
      AND PP.TIPOVIGENCIA IN ('RC')
      AND PP.REGALIAS IN(0)
GROUP BY DC.FECHA,                       
        PPC.CODIGO_SIA||DC.CUENTA,                                                   
       CN.TIPOPAGO_SIA,                                          
       FR.CODIGO_SIA,                                             
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE,                             
       T.NOMBRE,                                                  
       DC.TERCERO,                                                
       DC.DESCRIPCION,
      NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0),              
      NVL(EGRESOS_TESORERIA.RETENCION,0), 
      NVL(EGRESOS_TESORERIA.OTROS,0),
      EGRESOS_TESORERIA.CODBANCO_SIA,                         
      EGRESOS_TESORERIA.CUENTANUMERO,                            
      EGRESOS_TESORERIA.NRO_DOCUMENTO
