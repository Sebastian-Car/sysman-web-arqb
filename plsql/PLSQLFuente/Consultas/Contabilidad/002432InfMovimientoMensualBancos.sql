MERGE INTO CONSULTAS FIN USING (SELECT '002432InfMovimientoMensualBancos' INFORME ,TO_CLOB(q'[SELECT CODIGO, 
       NOMBRE, 
       SALDOANTERIORTOTAL,
       DEBITO,
       CREDITO,
       SALDONUETOTAL,
       (CASE WHEN CODIGO = '1110' THEN SALDONUETOTAL ELSE 0 END) SALDOMAYOR
  FROM (
    SELECT CODIGO, 
           NOMBRE,    
           SALDO,
           (((CASE WHEN NATURALEZA='D' and SALDO_1 >=0 THEN SALDO_1 ELSE 0 END) +
                (CASE WHEN NATURALEZA='C' And SALDO_1<0 THEN -SALDO_1 ELSE 0 END)) - 
           ((CASE WHEN NATURALEZA='C' And SALDO_1>=0 THEN SALDO_1 ELSE 0 END) + 
                (CASE WHEN NATURALEZA='D'  And SALDO_1 <0 THEN -SALDO_1 ELSE 0 END))) SALDOANTERIORTOTAL,
           DEBITO,
           CREDITO,  
           DEBITO_1,
           CREDITO_1,  
           (((CASE WHEN NATURALEZA='D' And SALDO>=0 THEN SALDO ELSE 0 END ) + 
                (CASE WHEN NATURALEZA='C' And SALDO<0 THEN -SALDO ELSE 0 END)) - 
           ((CASE WHEN NATURALEZA='C' And SALDO>=0 THEN SALDO ELSE 0 END) + 
                (CASE WHEN NATURALEZA='D' And SALDO<0 THEN -SALDO ELSE 0 END))) AS SALDONUETOTAL
      FROM (SELECT     
                CASE 
                  WHEN s$mes$s IN(1)  THEN PLAN_CONTABLE.SALDO1
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.SALDO2
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.SALDO3
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.SALDO4
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.SALDO5
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.SALDO6
                  WHEN s$mes$s IN(7)  THEN PLAN_CONTABLE.SALDO7
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.SALDO8
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.SALDO9
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.SALDO10
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.SALDO11
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.SALDO12
                ELSE 0
             END AS SALDO,
            PLAN_CONTABLE.COMPANIA,
            PLAN_CONTABLE.ANO,
            CASE 
                  WHEN s$mes$s IN(1)  THEN PLAN_CONTABLE.SALDO0
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.SALDO1
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.SALDO2
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.SALDO3
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.SALDO4
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.SALDO5
                  WHEN s$mes$s IN(7)  THEN PLAN_CONTABLE.SALDO6
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.SALDO7
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.SALDO8
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.SALDO9
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.SALDO10
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.SALDO11
                ELSE 0
             END AS SALDO_1,
            PLAN_CONTABLE.CODIGO, 
            PLAN_CONTABLE.NOMBRE,
            CASE 
                  WHEN s$mes$s IN(1)  THEN PLA]') || TO_CLOB(q'[N_CONTABLE.DEBITO1
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.DEBITO2
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.DEBITO3
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.DEBITO4
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.DEBITO5
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.DEBITO6
                  WHEN s$mes$s IN(7)  THEN PLAN_CONTABLE.DEBITO7
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.DEBITO8
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.DEBITO9
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.DEBITO10
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.DEBITO11
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.DEBITO12
                ELSE 0
             END AS DEBITO,
             CASE 
                  WHEN s$mes$s IN(1)  THEN PLAN_CONTABLE.CREDITO1
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.CREDITO2
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.CREDITO3
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.CREDITO4
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.CREDITO5
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.CREDITO6
                  WHEN s$mes$s IN(7)  THEN PLAN_CONTABLE.CREDITO7
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.CREDITO8
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.CREDITO9
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.CREDITO10
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.CREDITO11
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.CREDITO12
                ELSE 0
             END AS CREDITO,
             CASE 
                  WHEN s$mes$s IN(1)  THEN PLAN_CONTABLE.DEBITO0
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.DEBITO1
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.DEBITO2
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.DEBITO3
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.DEBITO4
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.DEBITO5
                  WHEN s$mes$s IN(7)  THEN PLAN_CONTABLE.DEBITO6
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.DEBITO7
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.DEBITO8
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.DEBITO9
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.DEBITO10
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.DEBITO11
                ELSE 0
             END AS DEBITO_1,
             CASE 
                  WHEN s$mes$s IN(1)  THEN PLAN_CONTABLE.CREDITO0
                  WHEN s$mes$s IN(2)  THEN PLAN_CONTABLE.CREDITO1
                  WHEN s$mes$s IN(3)  THEN PLAN_CONTABLE.CREDITO2
                  WHEN s$mes$s IN(4)  THEN PLAN_CONTABLE.CREDITO3
                  WHEN s$mes$s IN(5)  THEN PLAN_CONTABLE.CREDITO4
                  WHEN s$mes$s IN(6)  THEN PLAN_CONTABLE.CREDITO5
                  WHEN s$m]') || TO_CLOB(q'[es$s IN(7)  THEN PLAN_CONTABLE.CREDITO6
                  WHEN s$mes$s IN(8)  THEN PLAN_CONTABLE.CREDITO7
                  WHEN s$mes$s IN(9)  THEN PLAN_CONTABLE.CREDITO8
                  WHEN s$mes$s IN(10) THEN PLAN_CONTABLE.CREDITO9
                  WHEN s$mes$s IN(11) THEN PLAN_CONTABLE.CREDITO10
                  WHEN s$mes$s IN(12) THEN PLAN_CONTABLE.CREDITO11
                ELSE 0
             END AS CREDITO_1,
            PLAN_CONTABLE.NATURALEZA
     FROM PLAN_CONTABLE
    WHERE PLAN_CONTABLE.COMPANIA = 's$compania$s'
      AND PLAN_CONTABLE.ANO = s$anio$s
      AND PLAN_CONTABLE.CLASECUENTA = 'B'
    ORDER BY PLAN_CONTABLE.COMPANIA, 
             PLAN_CONTABLE.ANO, 
             PLAN_CONTABLE.CODIGO))
WHERE (CASE WHEN s$cuentasSinSaldo$s IN (0) THEN SALDOANTERIORTOTAL 
            WHEN s$cuentasSinSaldo$s IN (0) AND SALDOANTERIORTOTAL IN(0) THEN (DEBITO + CREDITO + DEBITO_1 + CREDITO_1)
           ELSE SALDO 
       END)  <> 0]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);