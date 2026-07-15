SELECT 
       SALDOAUX.COMPANIA,  
       CASE WHEN SUBSTR(SALDOAUX.CODIGO,1,1) = '0' 
               THEN 'z' || SALDOAUX.CODIGO 
               ELSE SALDOAUX.CODIGO 
       END  CLASEORDEN,  
       SUBSTR(SALDOAUX.CODIGO, 1, 1)  CLASE,  
       SALDOAUX.CODIGO,  
       SALDOAUX.TERCERO TERCERO, 
       SALDOAUX.AUXILIAR AUXILIAR, 
       SALDOAUX.CENTRO_COSTO CENTRO_COSTO, 
       SALDOAUX.ANO, 
       SALDOAUX.SALDOs$mesTrabajo$s SALDO, 
       SALDOAUX.NOMBRE,  
       (CASE WHEN SALDOAUX.Naturaleza='D' And Saldos$mesTrabajo$s>=0 THEN Saldos$mesTrabajo$s ELSE 0 END)+ 
       (CASE WHEN SALDOAUX.Naturaleza='C' And Saldos$mesTrabajo$s<0 THEN -Saldos$mesTrabajo$s ELSE 0 END) SaldoNueDebito, 
       (CASE WHEN SALDOAUX.Naturaleza='C' And Saldos$mesTrabajo$s>=0 THEN Saldos$mesTrabajo$s ELSE 0 END )+ 
       (CASE WHEN SALDOAUX.Naturaleza='D' And Saldos$mesTrabajo$s<0 THEN -Saldos$mesTrabajo$s ELSE 0 END ) SaldoNueCredito,  
       CASE WHEN SUBSTR(SALDOAUX.CODIGO,1,1) = '0' 
                 THEN 'Z' || SALDOAUX.CODIGO 
                 ELSE SALDOAUX.CODIGO END  ORDEN,  
       SALDOAUX.ORDEN  ORDENCUENTAS,
       SALDOAUX.REFERENCIA REFERENCIA,
       SALDOAUX.FUENTE_RECURSOS FUENTE_RECURSO
FROM (s$baseBalance$s) SALDOAUX 
WHERE LENGTH(SALDOAUX.CODIGO) <= s$digitos$s
  s$condicion$s
