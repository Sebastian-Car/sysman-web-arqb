MERGE INTO CONSULTAS FIN USING (SELECT '800612INGDEP_SALUD' INFORME ,TO_CLOB(q'[SELECT    CODIGO                  codigoContable,
          SALDOFINAL              IngresoTotal,   
          SALDOFINAL              IngresoSalud
                       
    FROM (   
        SELECT     COD_EQUIV CODIGO,                                                                                         
                   SUM (CASE WHEN 0  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )SALDOFINAL                                                                                
                                                                                     
           FROM  PLAN_CONTABLE
           WHERE PLAN_CONTABLE.COMPANIA =s$compania$s
             AND PLAN_CONTABLE.ANO      =s$ano$s
             AND CLASECUENTA IN('N')
             AND COD_EQUIV IS NOT NULL
             GROUP BY COD_EQUIV
            
           )
WHERE    ABS(SALDOFINAL) NOT IN (0)
ORDER BY  CODIGO]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);