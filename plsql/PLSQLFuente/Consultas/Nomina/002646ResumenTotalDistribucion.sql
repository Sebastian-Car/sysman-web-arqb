MERGE INTO CONSULTAS FIN USING (SELECT '002646ResumenTotalDistribucion' INFORME ,TO_CLOB(q'[SELECT DISTINCT Historicos.ID_de_Concepto, 
Conceptos.Nombre_Concepto, 
Conceptos.Clase, 
Sum(Historicos.Valor) AS Suma_de_valor, 
Conceptos.Relacionado, 
CASE Conceptos.CLASE WHEN 3 THEN 'VALOR DEVENGOS'
                        WHEN 5 THEN 'VALOR DESCUENTOS' 
                        WHEN 4 THEN'VALOR TOTAL DEVENGOS'
                        WHEN 6 THEN 'VALOR TOTAL DESCUENTOS'
                        WHEN 7 THEN 'VALOR NETO A PAGAR' 
                        ELSE 'CONCEPTOS CONTABLES'
                     END  CLASECONCEPTO,
                     Personal.Escalafon
FROM Historicos 
LEFT JOIN Periodos 
ON Historicos.Periodo = Periodos.Periodo
AND Historicos.Mes = Periodos.Mes
AND Historicos.Ano = Periodos.Ano
AND Historicos.ID_de_Proceso = Periodos.ID_de_Proceso
AND Historicos.Compania = Periodos.Compania
LEFT JOIN Conceptos 
ON Historicos.Compania = Conceptos.Compania
AND Historicos.ID_de_Concepto = Conceptos.ID_de_Concepto
LEFT JOIN Personal 
ON Historicos.ID_de_Empleado = Personal.ID_de_Empleado
AND Historicos.Compania = Personal.Compania
WHERE Historicos.Compania = s$compania$s
AND (LPAD(Historicos.ID_DE_PROCESO,2,0)||LPAD(Historicos.ANO,4,0) || LPAD(Historicos.MES,2,0) || LPAD(Historicos.PERIODO, 2,0)) BETWEEN LPAD(s$proceso$s,2,0)|| 
                                    LPAD(s$anio1$s,4,0)||
                                    LPAD (s$mes1$s,2,0) ||
                                    LPAD (s$periodo1$s,2,0) 
                                AND LPAD(s$proceso$s,2,0)||
                                    LPAD(s$anio2$s,4,0)|| 
                                    LPAD(s$mes2$s,2,0)||
                                    LPAD(s$periodo2$s,2,0)
s$depencia$s 
s$grupoContable$s 
GROUP BY HISTORICOS.ID_DE_CONCEPTO, CONCEPTOS.NOMBRE_CONCEPTO, CONCEPTOS.CLASE, CONCEPTOS.RELACIONADO, CASE CONCEPTOS.CLASE WHEN 3 THEN 'VALOR DEVENGOS' WHEN 5 THEN 'VALOR DESCUENTOS' WHEN 4 THEN'VALOR TOTAL DEVENGOS' WHEN 6 THEN 'VALOR TOTAL DESCUENTOS' WHEN 7 THEN 'VALOR NETO A PAGAR' ELSE 'CONCEPTOS CONTABLES' END, 
CONCEPTOS.CLASE, 3, 'VALOR DEVENGOS', 5, 'VALOR DESCUENTOS', 
4, 'VALOR TOTAL DEVENGOS', 6, 'VALOR TOTAL DESCUENTOS', 7, 
'VALOR NETO A PAGAR', 'CONCEPTOS CONTABLES', PERSONAL.ESCALAFON
HAVING (CONCEPTOS.CLASE = 3 OR CONCEPTOS.CLASE = 4 OR CONCEPTOS.CLASE = 5 OR CONCEPTOS.CLASE = 6 OR CONCEPTOS.CLASE = 7 OR CONCEPTOS.CLASE=8)
ORDER BY HISTORICOS.ID_DE_CONCEPTO]') CONSULTA, 6 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);