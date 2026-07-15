MERGE INTO CONSULTAS FIN USING (SELECT '800584Formato1001_BaseGravable' INFORME ,TO_CLOB(q'[SELECT CONCEPTO,
       TIPO_DE_IDENTIFICACION,
       NUMERO_IDENTIFICACION,      
       PRIMER_APELLIDO_DEL_INFORMADO,     
       SEGUNDO_APELLIDO_DEL_INFORMADO,
       PRIMER_NOMBRE_DEL_INFORMADO,
       OTROS_NOMBRES_DEL_INFORMADO,
       RAZON_SOCIAL_INFORMADO,
       DIRECCION,
       CODIGO_DPTO,
       CODIGO_MCP,
       CASE WHEN s$ckPago$s = -1 THEN PAGOS.PAGOS_DEDUCIBLE - IVA_DEDUCIBLE
            ELSE PAGOS.PAGOS_DEDUCIBLE
       END AS PAGO_ABONO_CUENTA_DEDUCIBLE,
       
        CASE WHEN s$ckPago$s = -1 AND s$contribuyente$s = -1 
            THEN PAGOS.PAGOS_NO_DEDUCIBLE - IVA__NO_DEDUCIBLE
        ELSE 
            PAGOS.PAGOS_NO_DEDUCIBLE
      END AS PAGO_ABONO_CUENTA_NO_DEDUCIBLE,     
      
       IVA_DEDUCIBLE,
       IVA__NO_DEDUCIBLE,
       RETEFUENTE_PRACTICADA_RENTA, 
       RETEFUENTE_ASUMIDA_RENTA,    
       RETEFUENTE_IVA_REGIMEN_COMUN,    
       RETEFUENTE_IVA_NO_DOMICILIADOS   
FROM(
        SELECT CONCEPTO_EX                                            CONCEPTO,
                TIPOS_DOCUMENTOS.CODIGODIAN                           TIPO_DE_IDENTIFICACION,
                DETALLE_COMPROBANTE_CNT.TERCERO                       NUMERO_IDENTIFICACION,
                CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.APELLIDO1
                 END                                                  PRIMER_APELLIDO_DEL_INFORMADO,
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.APELLIDO2
                 END                                                 SEGUNDO_APELLIDO_DEL_INFORMADO,
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.NOMBRE1
                 END                                                  PRIMER_NOMBRE_DEL_INFORMADO,
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.NOMBRE2
                 END                                                  OTROS_NOMBRES_DEL_INFORMADO,
               CASE WHEN TERCERO.TIPOID='N'
                       THEN TERCERO.NOMBRE
                 END                                                  RAZON_SOCIAL_INFORMADO,
              TERCERO.DIRECCION                                       DIRECCION,
              TERCERO.DEPARTAMENTO                                    CODIGO_DPTO,
              TERCERO.CIUDAD                                          CODIGO_MCP,
              PAISES.CODIGODIAN                                       PAIS_DE_RESIDENCIA_O_DOMICILIO,

              CASE WHEN(  s$contribuyente$s = -1) THEN
              0
              ELSE
              SUM(CASE WHEN PLAN_CONTABLE.IVACOMUN NOT IN(0)
                         AND COMPROBANTE_CNT.DEDUCIBLE IN(0)
                    THEN (BASE_IVA)
                    ELSE 0
                    END)
              END                                                     IVA_DEDUCIBLE,
              CASE WHEN(  s$contribuyente$s = -1) THEN
               SUM(CASE WHEN PLAN_CONTABLE.IVACOMUN NOT IN(0)
          ]') || TO_CLOB(q'[               AND COMPROBANTE_CNT.DEDUCIBLE IN(0)
                    THEN (BASE_IVA)
                    ELSE 0
                    END)
             ELSE
             SUM(CASE WHEN PLAN_CONTABLE.IVACOMUN NOT IN(0)
                         AND COMPROBANTE_CNT.DEDUCIBLE NOT IN(0)
                    THEN (DETALLE_COMPROBANTE_CNT.BASE_IVA)
                    ELSE 0
                    END) 
             END                                                       IVA__NO_DEDUCIBLE,
             SUM(CASE WHEN PLAN_CONTABLE.RETEPRACTICADA NOT IN(0)
                THEN (VALOR_CREDITO-VALOR_DEBITO)
                ELSE 0
                END)                                                   RETEFUENTE_PRACTICADA_RENTA, 
             SUM(CASE WHEN PLAN_CONTABLE.RETEASUMIDA NOT IN(0)
              THEN (VALOR_CREDITO-VALOR_DEBITO)
              ELSE 0
              END)                                                    RETEFUENTE_ASUMIDA_RENTA,    
              SUM(CASE WHEN PLAN_CONTABLE.IVACOMUN NOT IN(0)
              THEN (VALOR_CREDITO-VALOR_DEBITO)
              ELSE 0
              END)                                                     RETEFUENTE_IVA_REGIMEN_COMUN,    
             SUM(CASE WHEN PLAN_CONTABLE.IVACOMUN  NOT IN(0)
                  AND TERCERO.RESIDENTE_TRIBUTARIO NOT IN(0)
              THEN (VALOR_CREDITO-VALOR_DEBITO)
              ELSE 0
              END)                                                     RETEFUENTE_IVA_NO_DOMICILIADOS     

        FROM DETALLE_COMPROBANTE_CNT INNER JOIN TERCERO
                   ON  DETALLE_COMPROBANTE_CNT.COMPANIA=TERCERO.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.TERCERO=TERCERO.NIT
                   AND DETALLE_COMPROBANTE_CNT.SUCURSAL=TERCERO.SUCURSAL
              LEFT JOIN TIPOS_DOCUMENTOS
                   ON   TERCERO.COMPANIA=TIPOS_DOCUMENTOS.COMPANIA
                   AND  TERCERO.TIPOID  =TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
             LEFT JOIN PAISES
                    ON TERCERO.PAIS=PAISES.PAIS
             INNER JOIN PLAN_CONTABLE
                    ON  DETALLE_COMPROBANTE_CNT.COMPANIA=PLAN_CONTABLE.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO=PLAN_CONTABLE.ANO
                   AND DETALLE_COMPROBANTE_CNT.CUENTA=PLAN_CONTABLE.CODIGO
           INNER JOIN  CONCEPTOSEX
                   ON DETALLE_COMPROBANTE_CNT.CONCEPTO_EX=CONCEPTOSEX.CODIGO
               AND  DETALLE_COMPROBANTE_CNT.formato_concepto_ex=CONCEPTOSEX.FORMATO 

           INNER JOIN COMPROBANTE_CNT
                  ON  DETALLE_COMPROBANTE_CNT.COMPANIA=COMPROBANTE_CNT.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO=COMPROBANTE_CNT.ANO
                   AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=COMPROBANTE_CNT.TIPO
                   AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=COMPROBANTE_CNT.NUMERO 

        WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
        AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
        AND DETALLE_COMPROBANTE_CNT.CONCEPTO_EX NOT IN('5001']') || TO_CLOB(q'[) 
        AND DETALLE_COMPROBANTE_CNT.CONCEPTO_EX IS NOT NULL
        AND TERCERO.IND_REPORTAR_2276  IN(0)
        AND TERCERO.IND_PAGO_RETENCION  IN(0)
        GROUP BY CONCEPTO_EX,                                         
                TIPOS_DOCUMENTOS.CODIGODIAN,                          
                DETALLE_COMPROBANTE_CNT.TERCERO,                     
                CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.APELLIDO1
                 END,                                                  
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.APELLIDO2
                 END,                                                 
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.NOMBRE1
                 END,                                                 
               CASE WHEN TERCERO.TIPOID='C'
                       THEN TERCERO.NOMBRE2
                 END,                                                  
               CASE WHEN TERCERO.TIPOID='N'
                       THEN TERCERO.NOMBRE
                 END ,                                                 
              TERCERO.DIRECCION ,                                      
              TERCERO.DEPARTAMENTO ,                                   
              TERCERO.CIUDAD ,                                         
              PAISES.CODIGODIAN                                  
        )  BASE_EXOGENA
INNER JOIN ( 

    SELECT TERCERO,FORMATO_CONCEPTO_EX,CONCEPTO_EX, 
    CASE WHEN(  s$contribuyente$s = -1) THEN
    0
    ELSE
    SUM(CASE WHEN  BASE_DEDUCIBLE>0 
    THEN BASE_DEDUCIBLE 
    else CASE WHEN  PAGO_DEDUCIBLE>0  AND  BASE_DEDUCIBLE=0
    THEN PAGO_DEDUCIBLE 
    ELSE 0 
    END END )END                                             PAGOS_DEDUCIBLE,
    CASE WHEN(  s$contribuyente$s = -1) THEN
    SUM(CASE WHEN  BASE_DEDUCIBLE>0 
    THEN BASE_DEDUCIBLE 
    else CASE WHEN  PAGO_DEDUCIBLE>0  AND  BASE_DEDUCIBLE=0
    THEN PAGO_DEDUCIBLE 
    ELSE 0 
    END END )
    ELSE
    SUM(CASE WHEN  BASE_NO_DEDUCIBLE>0 
    THEN BASE_NO_DEDUCIBLE 
    else CASE WHEN    PAGO_NO_DEDUCIBLE>0    AND BASE_NO_DEDUCIBLE=0
    THEN PAGO_NO_DEDUCIBLE 
    ELSE 0 
    END END )
    END                                                     PAGOS_NO_DEDUCIBLE

    FROM 
    (
    select  detalle_comprobante_cnt.TERCERO,FORMATO_CONCEPTO_EX,CONCEPTO_EX,tipo_cpte,comprobante, 
     SUM(CASE WHEN COMPROBANTE_CNT.DEDUCIBLE IN(0)  AND clasecuenta IN ('I')
      THEN base_gravable
      ELSE 0 END )BASE_DEDUCIBLE,
     SUM(CASE WHEN COMPROBANTE_CNT.DEDUCIBLE NOT IN(0)  AND clasecuenta IN ('I')
      THEN base_gravable
      ELSE 0 END )BASE_NO_DEDUCIBLE, 
     SUM(CASE WHEN COMPROBANTE_CNT.DEDUCIBLE IN(0) AND clasecuenta NOT IN ('I') 
     THEN  VALOR_DEBITO- VALOR_CREDITO
     ELSE 0 END) PAGO_DEDUCIBLE,
     SUM(CASE WHEN COMPROBANTE_CNT.DEDUCIBLE NOT IN(0)  AND clasecuenta NOT IN ('I')
     THEN  VALOR_DEBITO- VALO]') || TO_CLOB(q'[R_CREDITO
     ELSE 0 END) PAGO_NO_DEDUCIBLE
       from PLAN_CONTABLE  INNER JOIN detalle_comprobante_cnt
    ON PLAN_CONTABLE.COMPANIA=detalle_comprobante_cnt.COMPANIA
    AND  PLAN_CONTABLE.ano=detalle_comprobante_cnt.ano
    AND  PLAN_CONTABLE.codigo=detalle_comprobante_cnt.cuenta
    inner join TIPO_COMPROBANTE
   ON TIPO_COMPROBANTE.COMPANIA=detalle_comprobante_cnt.COMPANIA
    AND  TIPO_COMPROBANTE.CODIGO=detalle_comprobante_cnt.TIPO_CPTE
   INNER JOIN COMPROBANTE_CNT
    ON COMPROBANTE_CNT.COMPANIA=detalle_comprobante_cnt.COMPANIA
     AND COMPROBANTE_CNT.ANO=detalle_comprobante_cnt.ANO
     AND COMPROBANTE_CNT.TIPO=detalle_comprobante_cnt.TIPO_CPTE
     AND COMPROBANTE_CNT.NUMERO=detalle_comprobante_cnt.COMPROBANTE
     INNER JOIN TERCERO
                   ON  DETALLE_COMPROBANTE_CNT.COMPANIA=TERCERO.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.TERCERO=TERCERO.NIT
                   AND DETALLE_COMPROBANTE_CNT.SUCURSAL=TERCERO.SUCURSAL
    WHERE PLAN_CONTABLE.ano=s$ano$s
    AND PLAN_CONTABLE.COMPANIA=s$compania$s
    AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
    AND TERCERO.IND_REPORTAR_2276  IN(0)
    AND TERCERO.IND_PAGO_RETENCION  IN(0)
    AND  CONCEPTO_EX IS NOT NULL
    GROUP BY  FORMATO_CONCEPTO_EX,CONCEPTO_EX,tipo_cpte,comprobante,detalle_comprobante_cnt.TERCERO)
         GROUP BY TERCERO,FORMATO_CONCEPTO_EX,CONCEPTO_EX) PAGOS
 ON BASE_EXOGENA.CONCEPTO =PAGOS.CONCEPTO_EX
 AND 1001=PAGOS.FORMATO_CONCEPTO_EX
 AND BASE_EXOGENA.NUMERO_IDENTIFICACION=PAGOS.TERCERO

]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);