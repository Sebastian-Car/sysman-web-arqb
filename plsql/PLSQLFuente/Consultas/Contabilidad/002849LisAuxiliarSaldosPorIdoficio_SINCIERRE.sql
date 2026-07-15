MERGE INTO CONSULTAS FIN USING (SELECT '002849LisAuxiliarSaldosPorIdoficio_SINCIERRE' INFORME ,TO_CLOB(q'[SELECT  NOCOMP,
       NOANO,
       ID,
       VD,
       VC,
       TIPO_CPTE,
       COMPROBANTE,
       CONSECUTIVO,
       FECHA,
       HORA,
       DESCRIPCION,
       NODOCUMENTO,
       DESCRIP,
       NOMBRETERCERO,
       0 SALDOACUMULA,
       SALDOINI + SUM(SALDOALDIA) SALDOINICIAL,       
       NOMBRECODIGO,
       SUM(SALDO) SALDO,
       ORDENIMP,
       CODIGOREF,
       TERCERO
from (
SELECT 
       P.COMPANIA NOCOMP,
       P.ANO NOANO, 
       CASE WHEN 's$id_codigo$s'='CODIGO' THEN P.CODIGO 
       ELSE TRIM(RPAD(SA.CODIGO,32)
          || RPAD(CASE WHEN MAN_CEN_CTO IN(0) THEN ' ' ELSE SA.CENTRO_COSTO   END  ,20) 
          || RPAD(CASE WHEN MAN_AUX_TER IN(0) THEN ' ' ELSE SA.TERCERO        END  ,18) 
          || RPAD(CASE WHEN MAN_AUX_TER IN(0) THEN ' ' ELSE SA.SUCURSAL       END  ,3) 
          || RPAD(CASE WHEN MAN_AUX_GEN IN(0) THEN ' ' ELSE SA.AUXILIAR       END  ,20) 
          || RPAD(CASE WHEN MAN_AUX_REF IN(0) THEN ' ' ELSE SA.REFERENCIA     END  ,20) 
          || RPAD(CASE WHEN MAN_AUX_FUE IN(0) THEN ' ' ELSE SA.FUENTE_RECURSO END  ,20) 
          ) 
       END ID,
       COALESCE(DE.VALOR_DEBITO,0) VD, 
       COALESCE(DE.VALOR_CREDITO,0) VC,
       DE.TIPO_CPTE,
       DE.COMPROBANTE, 
       DE.CONSECUTIVO,
       DE.FECHA,
       DE.HORA,
       DE.DESCRIPCION_CNT DESCRIPCION, 
       DE.NRO_DOCUMENTO NODOCUMENTO,
       NVL(DE.DESCRIPCION_DET,DE.DESCRIPCION_CNT) DESCRIP,       
       CASE WHEN 's$id_codigo$s'='CODIGO' AND DE.COMPANIA IS NULL THEN 
            ' '  
       ELSE COALESCE(DE.TERCERO_NOM, TERCERO_SA.NOMBRE)
       END NOMBRETERCERO,
       CASE WHEN 's$id_codigo$s'='CODIGO' THEN P.SALDOs$mesAnterior$s ELSE SA.SALDOs$mesAnterior$s END SALDOINI,

       NVL(DIARIO.SALDOALDIA,0) SALDOALDIA,       

       CASE WHEN 's$id_codigo$s'='CODIGO' THEN P.NOMBRE
       ELSE P.NOMBRE ||
            CASE WHEN  P.MAN_AUX_TER <>0 THEN '/' || TERCERO_SA.NOMBRE ELSE NULL END  ||
            CASE WHEN  P.MAN_AUX_GEN <>0 THEN '/' || AUXILIAR_SA.NOMBRE ELSE NULL END ||
            CASE WHEN  P.MAN_CEN_CTO <>0 THEN '/' || CENTRO_COSTO_SA.NOMBRE ELSE NULL END  ||
            CASE WHEN  P.MAN_AUX_REF <>0 THEN '/' || REFERENCIA_SA.NOMBRE ELSE NULL END  ||
            CASE WHEN  P.MAN_AUX_FUE <>0 THEN '/' || FUENTE_RECURSOS_SA.NOMBRE ELSE NULL END
       END NOMBRECODIGO,
       CASE WHEN INSTR('ILB',DE.CLASE_CONTABLE)<>0 THEN 'E' ELSE 'S' END ORDENIMP,
       COALESCE(CASE WHEN SA.NATURALEZA='D' THEN 1 ELSE -1 END * (DE.VALOR_DEBITO-DE.VALOR_CREDITO),0 ) SALDO, 
       SA.REFERENCIA CODIGOREF,

       CASE WHEN 's$id_codigo$s'='CODIGO' AND DE.COMPANIA IS NULL THEN 
            ' '  
       ELSE COALESCE(DE.TERCERO, SA.TERCERO)
       END TERCERO
FROM PLAN_CONTABLE P INNER JOIN   SALDO_AUX_CONTABLE SA 
  ON P.COMPANIA = SA.COMPANIA
  AND P.ANO     = SA.ANO
  AND P.CODIGO  = SA.CODIGO

   LEFT JOIN ( SELECT COMPANIA,ANO,CUENTA, SUM( CASE WHEN  TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY')=  TO_DATE('01'||'/'||TO_CHAR(TO_DATE]') || TO_CLOB(q'[('s$fechaInicial$s','DD/MM/YYYY'),'MM')||'/'||TO_CHAR(TO_DATE('s$fechaInicial$s','DD/MM/YYYY'),'YYYY'), 'DD/MM/YYYY' )THEN 0 ELSE  CASE WHEN DETALLE_COMPROBANTE_CNT.naturaleza = 'D' THEN DETALLE_COMPROBANTE_CNT.valor_debito - DETALLE_COMPROBANTE_CNT.valor_credito ELSE DETALLE_COMPROBANTE_CNT.valor_credito - DETALLE_COMPROBANTE_CNT.valor_debito END END )
                                        SALDOALDIA
                                     FROM DETALLE_COMPROBANTE_CNT
                                     WHERE COMPANIA= s$compania$s
                                     AND ANO= s$anio$s
                                     AND (DETALLE_COMPROBANTE_CNT.FECHA >= TO_DATE('01'||'/'||TO_CHAR(TO_DATE('s$fechaInicial$s','DD/MM/YYYY'),'MM')||'/'||TO_CHAR(TO_DATE('s$fechaInicial$s','DD/MM/YYYY'),'YYYY'), 'DD/MM/YYYY' ) AND  DETALLE_COMPROBANTE_CNT.FECHA <  TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY'))
                                     AND CUENTA BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s' 
                                     GROUP BY COMPANIA,ANO,CUENTA)DIARIO
                             ON P.COMPANIA = DIARIO.COMPANIA
                           AND P.ANO = DIARIO.ANO
                           AND P.CODIGO = DIARIO.CUENTA  
                LEFT JOIN 
                         (SELECT DETALLE_COMPROBANTE_CNT.COMPANIA, 
                                 DETALLE_COMPROBANTE_CNT.ANO,
                                 DETALLE_COMPROBANTE_CNT.CUENTA,
                                 DETALLE_COMPROBANTE_CNT.ID
,
                                 DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                 DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                 DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                 DETALLE_COMPROBANTE_CNT.CENTRO_COSTOI,
                                 DETALLE_COMPROBANTE_CNT.TERCEROI,
                                 DETALLE_COMPROBANTE_CNT.SUCURSALI,
                                 DETALLE_COMPROBANTE_CNT.AUXILIARI,
                                 DETALLE_COMPROBANTE_CNT.REFERENCIAI,
                                 DETALLE_COMPROBANTE_CNT.FUENTE_RECURSOI,
                                 DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                 DETALLE_COMPROBANTE_CNT.TERCERO,
                                 DETALLE_COMPROBANTE_CNT.SUCURSAL,
                                 DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                 DETALLE_COMPROBANTE_CNT.REFERENCIA,
                                 DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                 DETALLE_COMPROBANTE_CNT.MES,
                                 DETALLE_COMPROBANTE_CNT.HORA,
                                 DETALLE_COMPROBANTE_CNT.FECHA,                                 
                                  CASE WHEN DETALLE_COMPROBANTE_CNT.FECHA>= TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY' ) THEN
                                            DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
     ]') || TO_CLOB(q'[                            ELSE
                                        0
                                 END VALOR_DEBITO,
                                 CASE WHEN DETALLE_COMPROBANTE_CNT.FECHA>= TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY' ) THEN
                                            DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
                                 ELSE
                                        0
                                 END VALOR_CREDITO,
                                 DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                                 DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                                DETALLE_COMPROBANTE_CNT.DESCRIPCION DESCRIPCION_DET,
                                TIP.CLASE_CONTABLE,
                                CNT.NRO_DOCUMENTO NRO_DOCUMENTO_CNT,
                                CNT.DESCRIPCION DESCRIPCION_CNT,
                                TERCERO_DET.NOMBRE AS TERCERO_NOM
                          FROM COMPROBANTE_CNT CNT INNER JOIN TIPO_COMPROBANTE TIP
                            ON CNT.COMPANIA= TIP.COMPANIA
                           AND CNT.TIPO = TIP.CODIGO
                           INNER JOIN DETALLE_COMPROBANTE_CNT 
                            ON CNT.COMPANIA= DETALLE_COMPROBANTE_CNT.COMPANIA
                           AND CNT.ANO = DETALLE_COMPROBANTE_CNT.ANO
                           AND CNT.TIPO = DETALLE_COMPROBANTE_CNT.TIPO_CPTE
                           AND CNT.NUMERO = DETALLE_COMPROBANTE_CNT.COMPROBANTE
                           LEFT JOIN TERCERO TERCERO_DET
                           ON DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO_DET.COMPANIA
                           AND DETALLE_COMPROBANTE_CNT.TERCERO = TERCERO_DET.NIT
                           AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO_DET.SUCURSAL

                    WHERE CNT.COMPANIA= s$compania$s
                      AND CNT.ANO     = s$anio$s
                      AND CNT.TIPO  BETWEEN 's$tipoInicial$s' AND 's$tipoFinal$s'
                      AND CNT.FECHA BETWEEN TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s', 'DD/MM/YYYY' )
                      AND DETALLE_COMPROBANTE_CNT.CUENTA  BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s' 
                      AND TIP.CLASE_CONTABLE NOT IN('Z')
                      s$filtrosTercero$s
                      s$filtrosCentro$s
                      s$condicionReferencias$s
                         ) DE
                          ON SA.COMPANIA       = DE.COMPANIA
                         AND SA.ANO            = DE.ANO
                         AND SA.CODIGO            = DE.CUENTA
                         AND SA.CENTRO_COSTO   = DE.CENTRO_COSTOI
                         AND SA.TERCERO        = DE.TERCEROI
                         AND SA.SUCURSAL       = DE.SUCURSALI
                         AND SA.AUXILIAR       = DE.AUXILIARI
                         AND SA.REFERENCIA     = DE.REFERENCIAI
                         AND SA.FUENTE_RECURSO = DE]') || TO_CLOB(q'[.FUENTE_RECURSOI                         
    INNER JOIN CENTRO_COSTO CENTRO_COSTO_SA 
   ON  SA.COMPANIA     = CENTRO_COSTO_SA.COMPANIA 
   AND SA.ANO          = CENTRO_COSTO_SA.ANO 
   AND COALESCE(DE.CENTRO_COSTO, SA.CENTRO_COSTO) = CENTRO_COSTO_SA.CODIGO 
 INNER JOIN TERCERO TERCERO_SA
   ON  SA.COMPANIA = TERCERO_SA.COMPANIA 
   AND COALESCE(DE.TERCERO, SA.TERCERO) = TERCERO_SA.NIT
   AND COALESCE(DE.SUCURSAL, SA.SUCURSAL) = TERCERO_SA.SUCURSAL     
 INNER JOIN AUXILIAR AUXILIAR_SA
    ON  SA.COMPANIA = AUXILIAR_SA.COMPANIA 
    AND SA.ANO      = AUXILIAR_SA.ANO 
    AND COALESCE(DE.AUXILIAR, SA.AUXILIAR) = AUXILIAR_SA.CODIGO    
 INNER JOIN REFERENCIA REFERENCIA_SA
    ON  SA.COMPANIA   = REFERENCIA_SA.COMPANIA 
    AND SA.ANO        = REFERENCIA_SA.ANO 
    AND COALESCE(DE.REFERENCIA, SA.REFERENCIA) = REFERENCIA_SA.CODIGO
 INNER JOIN FUENTE_RECURSOS FUENTE_RECURSOS_SA
    ON  SA.COMPANIA       = FUENTE_RECURSOS_SA.COMPANIA 
    AND SA.ANO            = FUENTE_RECURSOS_SA.ANO 
    AND COALESCE(DE.FUENTE_RECURSO, SA.FUENTE_RECURSO)  = FUENTE_RECURSOS_SA.CODIGO   
WHERE P.COMPANIA = s$compania$s
  AND P.ANO      = s$anio$s
  AND P.CODIGO BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s' 
 AND (ABS(SA.SALDOs$mesAnterior$s) 
     + ABS(NVL(DIARIO.SALDOALDIA,0))
+ ABS(NVL(DE.VALOR_DEBITO,0))    
+ ABS(NVL(DE.VALOR_CREDITO,0))    
     ) <>0
) TABLA
s$filtrosTer$s
GROUP BY NOCOMP,
       NOANO,
       ID,
       VD,
       VC,
       TIPO_CPTE,
       COMPROBANTE,
       CONSECUTIVO,
       FECHA,
       HORA,
       DESCRIPCION,
       NODOCUMENTO,
       SALDOINI,
       DESCRIP,
       NOMBRETERCERO,
       NOMBRECODIGO,
       CODIGOREF,
       TERCERO,
       ORDENIMP]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);