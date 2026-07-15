SELECT DISTINCT CASE 's$nitCompania$s'  
                WHEN '890704536-7'  
                THEN CASE PERSONAL.GRUPOCONTABLE WHEN 'A' THEN 'ADMINISTRACION' 
                                                 WHEN 'V' THEN 'POLITICA' 
                                                 WHEN 'P' THEN 'GESTION' 
                                                 ELSE  'EJECUCION' END  
                ELSE CASE PERSONAL.GRUPOCONTABLE WHEN 'A' THEN 'ADMINISTRACION' 
                                                 WHEN 'V' THEN 'OPERACION/VENTAS' 
                                                 WHEN 'P' THEN 'PRODUCCION/PENSIONADO' 
                                                 ELSE 'OTRO' END END AS GRUPOCONTABLE, 
                V_ACUMULADOS.ID_DE_CONCEPTO,  
                V_ACUMULADOS.NOMBRE_CONCEPTO, 
                V_ACUMULADOS.NOMBRE_CENTRO_DE_COSTO, 
                V_ACUMULADOS.ID_CENTRO_DE_COSTO,  
                V_ACUMULADOS.CLASE, 
                SUM(V_ACUMULADOS.VALOR) AS SUMA_DE_VALOR, 
                CASE V_ACUMULADOS.CLASE WHEN 3 THEN 'VALOR DEVENGOS'
                                        WHEN  4 THEN'VALOR TOTAL DEVENGOS' 
                                        WHEN  5 THEN 'VALOR DESCUENTOS' 
                                        WHEN  6 THEN 'VALOR TOTAL DESCUENTOS' 
                                        WHEN  7 THEN 'VALOR NETO A PAGAR' 
                                        ELSE  'CONCEPTOS CONTABLES' END AS CLASECONCEPTO, 
                V_ACUMULADOS.NOMBRE_DEPENDENCIA AS NOMBRE, 
                V_ACUMULADOS.DEPENDENCIA 
FROM PERSONAL  
  LEFT JOIN V_ACUMULADOS  
    ON  PERSONAL.COMPANIA       = V_ACUMULADOS.COMPANIA  
    AND PERSONAL.ID_DE_EMPLEADO = V_ACUMULADOS.ID_DE_EMPLEADO  
    AND PERSONAL.DEPENDENCIA    = V_ACUMULADOS.DEPENDENCIA 
WHERE V_ACUMULADOS.COMPANIA= s$compania$s 
AND LPAD(V_ACUMULADOS.ID_DE_PROCESO,2,'0') || V_ACUMULADOS.ANO || LPAD(V_ACUMULADOS.MES,2,'0') || LPAD(V_ACUMULADOS.PERIODO,2,'0') 
BETWEEN LPAD(s$proceso$s,2,'0') || s$ano1$s || LPAD(s$mes1$s,2,'0') || LPAD(s$periodo1$s,2,'0')  
    AND LPAD(s$proceso$s,2,'0') || s$ano2$s || LPAD(s$mes2$s,2,'0') || LPAD(s$periodo2$s,2,'0')  
AND V_ACUMULADOS.CLASE In (3,4,5,6,7,8) 
s$CONDDEPENDENCIA$s 
GROUP BY CASE 's$nitCompania$s'  
         WHEN '890704536-7'  
         THEN CASE PERSONAL.GRUPOCONTABLE WHEN 'A' THEN 'ADMINISTRACION' 
                                                 WHEN 'V' THEN 'POLITICA' 
                                                 WHEN 'P' THEN 'GESTION' 
                                                 ELSE  'EJECUCION' END  
         ELSE CASE PERSONAL.GRUPOCONTABLE WHEN 'A' THEN 'ADMINISTRACION' 
                                                 WHEN 'V' THEN 'OPERACION/VENTAS' 
                                                 WHEN 'P' THEN 'PRODUCCION/PENSIONADO' 
                                                 ELSE 'OTRO' END END, 
         V_ACUMULADOS.ID_DE_CONCEPTO, 
         V_ACUMULADOS.NOMBRE_CONCEPTO, 
         V_ACUMULADOS.NOMBRE_CENTRO_DE_COSTO, 
         V_ACUMULADOS.ID_CENTRO_DE_COSTO, 
         V_ACUMULADOS.CLASE, 
         CASE V_ACUMULADOS.CLASE WHEN 3 THEN 'VALOR DEVENGOS'
                                        WHEN  4 THEN'VALOR TOTAL DEVENGOS' 
                                        WHEN  5 THEN 'VALOR DESCUENTOS' 
                                        WHEN  6 THEN 'VALOR TOTAL DESCUENTOS' 
                                        WHEN  7 THEN 'VALOR NETO A PAGAR' 
                                        ELSE  'CONCEPTOS CONTABLES' END, 
         V_ACUMULADOS.NOMBRE_DEPENDENCIA, 
         V_ACUMULADOS.DEPENDENCIA 
ORDER BY V_ACUMULADOS.ID_DE_CONCEPTO
