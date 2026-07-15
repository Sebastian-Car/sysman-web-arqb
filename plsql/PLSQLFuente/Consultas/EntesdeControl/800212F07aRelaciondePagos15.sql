SELECT 
     FechaDePago                                                  "|F| Fecha De Pago",
     CodigoPresupuestal                                           "|G| Código Presupuestal",
     ClaseDePago "|C| Clase De Pago",
     TipoDePago  "|C| Clase De Pago",
     NoDeComprobante "|C| No De Comprobante",
     Beneficiario  "|C| Beneficiario",
     CedulaONit    "|N| Cédula O Nit",
     DetalleDePago "|C| Detalle De Pago", 
     ValorComprobanteDePago "|D| Valor Comprobante De Pago",  
    CASE WHEN ROW_NUMBER() OVER(PARTITION BY NoDeComprobante ORDER BY NoDeComprobante) =1 
    THEN DescuentosSegSocial ELSE 0 END "|D| Descuentos Seg. Social",
    CASE WHEN ROW_NUMBER() OVER(PARTITION BY NoDeComprobante ORDER BY NoDeComprobante) =1 
    THEN DescuentosRetenciones ELSE 0 END "|D| Descuentos Retenciones",
    CASE WHEN ROW_NUMBER() OVER(PARTITION BY NoDeComprobante ORDER BY NoDeComprobante) =1 
    THEN OtrosDescuentos ELSE 0 END "|D| Otros Descuentos",
    NETOPAGO-(CASE WHEN ROW_NUMBER() OVER(PARTITION BY NoDeComprobante ORDER BY NoDeComprobante) =1 
                          THEN DescuentosSegSocial +  DescuentosRetenciones + OtrosDescuentos
                          ELSE 0 END )  "|D| Neto Pagado",
     Banco  "|C| Banco",   
     NoDeCuenta "|C| No De Cuenta" ,
    NoDeChequeONd	 "|C| No De Cheque O Nd" 
FROM (
SELECT TO_CHAR(DC.FECHA,'DD/MM/YYYY')                             FechaDePago,
       PPC.CODIGO_SIA||DC.CUENTA                                  CodigoPresupuestal,
       'ND'                                                       ClaseDePago,
       NVL(CN.TIPOPAGO_SIA,'ND')                                  TipoDePago,
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE                          NoDeComprobante,
       T.NOMBRE                                                   Beneficiario,
       DC.TERCERO                                                 CedulaONit,
       DC.DESCRIPCION                                             DetalleDePago,
       SUM(DC.VALOR_DEBITO-DC.VALOR_CREDITO)                      ValorComprobanteDePago, 
      NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0)                   DescuentosSegSocial,
      NVL(EGRESOS_TESORERIA.RETENCION,0)                          DescuentosRetenciones,
      NVL(EGRESOS_TESORERIA.OTROS,0)                              OtrosDescuentos,
      SUM(DC.VALOR_DEBITO)                                        NETOPAGO,          
      EGRESOS_TESORERIA.NOMBREBANCO                              Banco,  
      EGRESOS_TESORERIA.CUENTANUMERO                              NoDeCuenta ,
     NVL( EGRESOS_TESORERIA.NRO_DOCUMENTO,'ND')                   NoDeChequeONd	       
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
                 NVL(DESCUENTOS.SEGURIDAD_SOCIAL,0)+NVL(DESCUENTOSCOM.SEGURIDAD_SOCIAL,0) SEGURIDAD_SOCIAL,
                 NVL(DESCUENTOS.RETENCION,0) +NVL(DESCUENTOSCOM.RETENCION,0)  RETENCION,
                 NVL(DESCUENTOS.OTROS_DESCUENTOS,0)+ NVL(DESCUENTOSCOM.OTROS_DESCUENTOS,0) OTROS ,
                 CB.NOMBREBANCO,
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
       BANCO.NOMBREBANCO,
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
 INNER JOIN BANCO
     ON  BANCO.COMPANIA=CUENTABANCOS.COMPANIA
     AND BANCO.BANCO=CUENTABANCOS.BANCO
  WHERE ESTADO IN('A')) CB
               ON DC.COMPANIA=CB.COMPANIA
               AND DC.ANO=CB.ANO
               AND DC.CODIGO_CUENTA=CB.CUENTA
  AND DC.TIPO_CPTE=CB.TIPO_CPTE
  AND DC.COMPROBANTE=CB.COMPROBANTE
            
             LEFT JOIN (
			 SELECT DC.COMPANIA,
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
                LEFT JOIN ( SELECT COMPANIA,
								   ANO,
								   TIPO_CPTE,
								   COMPROBANTE,
								   SUM(RETENCION) RETENCION,
								   SUM(SEGURIDAD_SOCIAL) SEGURIDAD_SOCIAL,
								   SUM(OTROS_DESCUENTOS) OTROS_DESCUENTOS
							FROM
							(
								   SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
										 COMPROBANTE_CNTAFECTADOS.ANO,
										 COMPROBANTE_CNTAFECTADOS.TIPO_CPTE,
										 COMPROBANTE_CNTAFECTADOS.COMPROBANTE,
										 COMPROBANTE_CNTAFECTADOS.ANO_AFECT,
										 COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT,
										 COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT,
										 ROW_NUMBER() OVER(PARTITION BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT 
																	 ORDER BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT) REG,
										 
										 CASE WHEN ROW_NUMBER() OVER(PARTITION BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT 
																	 ORDER BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT)=1
											 THEN
											   SUM(CASE WHEN PLAN_CONTABLE.TIPODESCUENTO_SIA='RETENCION' 
												  THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
													  ELSE 0 END)
											ELSE 0 END RETENCION, 
										 CASE WHEN ROW_NUMBER() OVER(PARTITION BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT 
																	 ORDER BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT)=1
											 THEN
											   SUM(CASE WHEN PLAN_CONTABLE.TIPODESCUENTO_SIA='SEGURIDAD_SOCIAL'
													  THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
													  ELSE 0 END) 
											  ELSE 0 END  SEGURIDAD_SOCIAL,
										  CASE WHEN ROW_NUMBER() OVER(PARTITION BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT 
																	 ORDER BY COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT, COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT)=1
											 THEN
											  SUM(CASE WHEN PLAN_CONTABLE.TIPODESCUENTO_SIA='OTRO'
													THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
													ELSE 0 END)
										   ELSE 0 END  OTROS_DESCUENTOS
								  FROM DETALLE_COMPROBANTE_CNT INNER JOIN TIPO_COMPROBANTE
									  ON DETALLE_COMPROBANTE_CNT.COMPANIA=TIPO_COMPROBANTE.COMPANIA
									  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=TIPO_COMPROBANTE.CODIGO
								  INNER JOIN PLAN_CONTABLE 
									 ON DETALLE_COMPROBANTE_CNT.COMPANIA=PLAN_CONTABLE.COMPANIA
									 AND DETALLE_COMPROBANTE_CNT.CUENTA=PLAN_CONTABLE.CODIGO
									AND DETALLE_COMPROBANTE_CNT.ANO=PLAN_CONTABLE.ANO  
								  INNER JOIN COMPROBANTE_CNTAFECTADOS
								  ON DETALLE_COMPROBANTE_CNT.COMPANIA=COMPROBANTE_CNTAFECTADOS.COMPANIA
									 AND DETALLE_COMPROBANTE_CNT.ANO=COMPROBANTE_CNTAFECTADOS.ANO_AFECT
									 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT
									 AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT
								  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
								  AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
								  AND DETALLE_COMPROBANTE_CNT.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s
								  AND PLAN_CONTABLE.TIPODESCUENTO_SIA IS NOT NULL
								  AND TIPO_COMPROBANTE.CLASE_CONTABLE IN ('P')
								  GROUP BY DETALLE_COMPROBANTE_CNT.COMPANIA,
										   COMPROBANTE_CNTAFECTADOS.ANO,
										   COMPROBANTE_CNTAFECTADOS.TIPO_CPTE,
										   COMPROBANTE_CNTAFECTADOS.COMPROBANTE,
										   COMPROBANTE_CNTAFECTADOS.ANO_AFECT,
										   COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT,
										 COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT      
							)
							GROUP BY COMPANIA,
								   ANO,
								   TIPO_CPTE,
								   COMPROBANTE
		 )		 
		 
		 DESCUENTOSCOM            

		 ON DC.COMPANIA=DESCUENTOSCOM.COMPANIA
                    AND DC.ANO=DESCUENTOSCOM.ANO
                    AND DC.TIPO_CPTE=DESCUENTOSCOM.TIPO_CPTE
                    AND DC.COMPROBANTE=DESCUENTOSCOM.COMPROBANTE
                    
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
      AND PP.TIPOVIGENCIA NOT IN ('RC')
      AND PP.REGALIAS IN(0)
GROUP BY DC.FECHA,                          
       PPC.CODIGO_SIA||DC.CUENTA,                                                   
       NVL(CN.TIPOPAGO_SIA,'ND'),                                          
       FR.CODIGO_SIA,                                             
       DC.TIPO_CPTE||'-'||DC.COMPROBANTE,                             
       T.NOMBRE,                                                  
       DC.TERCERO,                                                
       DC.DESCRIPCION,
      NVL(EGRESOS_TESORERIA.SEGURIDAD_SOCIAL,0),              
      NVL(EGRESOS_TESORERIA.RETENCION,0), 
      NVL(EGRESOS_TESORERIA.OTROS,0),
      EGRESOS_TESORERIA.NOMBREBANCO,                         
      EGRESOS_TESORERIA.CUENTANUMERO,                            
      EGRESOS_TESORERIA.NRO_DOCUMENTO
 ORDER BY DC.TIPO_CPTE||'-'||DC.COMPROBANTE
)
ORDER BY FechaDePago
