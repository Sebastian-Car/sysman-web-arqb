MERGE INTO CONSULTAS FIN USING (SELECT '800523CONSULTADEDESCUENTOS' INFORME ,TO_CLOB(q'[ SELECT ConsultaDeDescuentosP.*
 FROM  
 (

                SELECT
                    Detalle_comprobante_cnt.cuenta,
                    detalle_comprobante_cnt.tipo_cpte TIPO_CPTE,
                    to_char(detalle_comprobante_cnt.comprobante) COMPROBANTE,
                    tercero.nombre NOMBRE,
                    to_char(detalle_comprobante_cnt.fecha, 'DD/MM/YYYY') FECHA, 
                    Detalle_comprobante_cnt.Valor_credito,
                    Detalle_comprobante_cnt.Valor_debito,                    
                    to_char(EGRESO) EGRESO
                FROM Tercero INNER JOIN Detalle_comprobante_cnt 
                LEFT JOIN Plan_contable
                         ON Detalle_comprobante_cnt.COMPANIA = Plan_contable.COMPANIA
                         AND Detalle_comprobante_cnt.ANO = Plan_contable.ANO 
                         AND Detalle_comprobante_cnt.CUENTA = Plan_contable.CODIGO
                         ON Tercero.NIT = Detalle_comprobante_cnt.TERCERO 
                         AND Tercero.SUCURSAL = Detalle_comprobante_cnt.SUCURSAL 
                         AND Tercero.COMPANIA = Detalle_comprobante_cnt.COMPANIA
                LEFT JOIN (

                                        SELECT distinct tipo_cpte TIPO_C,
                                            comprobante EGRESO,
                                            COMPANIA COMP,
                                            ANO,
                                            ANO_AFECT ANIO_AFEC,
                                            TIPO_CPTE_AFECT AFECTA,
                                            CMPTE_AFECTADO AFECT,
                                            fecha FECHA  
                                        FROM Detalle_comprobante_cnt 
                                        WHERE COMPANIA = s$compania$s
                                        AND  ano between substr('s$fechaInicial$s',7) and substr('s$fechaFinal$s',7)
                                        AND TIPO_CPTE_AFECT = 's$tipo$s'

                               ) TABLA
                              ON Detalle_comprobante_cnt.COMPANIA = TABLA.COMP
                              AND Detalle_comprobante_cnt.ANO =  TABLA.ANIO_AFEC

                              AND detalle_comprobante_cnt.comprobante = TABLA.AFECT
                              AND detalle_comprobante_cnt.tipo_cpte = TABLA.AFECTA
                 WHERE Detalle_comprobante_cnt.COMPANIA = s$compania$s 
                 AND Plan_contable.CLASECUENTA in 's$parametro$s' 
                 AND Detalle_comprobante_cnt.Tipo_cpte ='s$tipo$s'
                 AND  Detalle_comprobante_cnt.fecha BETWEEN TO_DATE('s$fechaInicial$s','DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s','DD/MM/YYYY')
                 ORDER BY Detalle_comprobante_cnt.cuenta, tercero.nombre 
 )      
 PIVOT(Sum(Valor_credito + Valor_debito) FOR CUENTA IN (s$pivot$s))
 ConsultaDeDescuentosP]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, 'mrosero' MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);