MERGE INTO CONSULTAS FIN USING (SELECT '800604DIS_FONDOS_SIVICOF' INFORME ,TO_CLOB(q'[SELECT SUBCODIGO, NOMBRE,
       SALDOFINAL,
       '' OBSERVACIONES 
FROM (
        SELECT  CASE WHEN CODIGO IN('1105')
                    THEN 'a) Cajas menores y principal'
                    ELSE CASE WHEN CODIGO IN('111005')
                    THEN 'b) Cuentas Corrientes'
                    ELSE CASE WHEN CODIGO IN('111006')
                    THEN 'c) Cuentas de Ahorro'
                    ELSE CASE WHEN CODIGO IN ('1132') 
                    THEN 'd) Inversiones Temporales'
                    ELSE CASE WHEN CODIGO IN('13') 
                    THEN 'e) Cuentas Corrientes'
                    ELSE CASE WHEN CODIGO IN('1319') 
                     THEN 'b) Inversiones'
                     ELSE CASE WHEN CODIGO IN('1386') 
                     THEN 'a) Fondos de Terceros'
                     ELSE   CASE WHEN CODIGO IN('15') 
                     THEN 'b) Recaudos de Terceros'
                      ELSE   CASE WHEN CODIGO IN('1505') 
                      THEN  'c) Tesorerías de terceros'
                      ELSE CASE WHEN CODIGO IN('1514')
                      THEN 'III- INVERSIONES PERMANENTES'
                      ELSE CASE WHEN CODIGO IN('1525')
                      THEN 'IV- RECURSOS COMPROMETIDOS'
                     END END END END END END END END END END END  NOMBRE,   
                     CASE WHEN PLAN_CONTABLE.CODIGO IN ('1105','111005','111006')
                     THEN (CASE WHEN 0  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )
                           ELSE  0 END SALDOFINAL,
                    CASE WHEN CODIGO IN ('1105','111005','111006','1132') THEN 1 ELSE 
                    CASE WHEN CODIGO IN ('13','1319') THEN 2 ELSE
                    CASE WHEN CODIGO IN ]') || TO_CLOB(q'[('1386','15','1505')THEN 3 ELSE
                    4
                    END END END AS SUBCODIGO
           FROM  PLAN_CONTABLE        
           WHERE PLAN_CONTABLE.COMPANIA =s$compania$s
             AND PLAN_CONTABLE.ANO      =s$ano$s
            AND PLAN_CONTABLE.CODIGO IN ('1105','111005','111006','1132','13','1319','1386','15','1505','1514','1525')
    ORDER BY CODIGO                 
)]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);