SELECT  SUBSTR(SALDOAUX.CODIGO,1,1)  CLASE, 
        SALDOAUX.CODIGO, 
        SALDOAUX.NOMBRE,  
        SALDOAUX.TERCERO,
        SALDOAUX.COMPANIA, 
        SALDOAUX.SALDO0   AS SALDO_1,
        s$strDebitoSum$s  AS DEBITO,
        s$strCreditoSum$s AS CREDITO,
        SALDOAUX.DEBITO0  AS DEBITO_1, 
        SALDOAUX.CREDITO0 AS CREDITO_1 ,
        SALDOAUX.NETO0, 
        SUBSTR((CASE WHEN SUBSTR(SALDOAUX.CODIGO,1,1)='0'
             THEN 'z'||SALDOAUX.CODIGO
             ELSE SALDOAUX.CODIGO
             END),1,1) AS CLASEORDEN,
        SALDOAUX.CODIGO||CASE WHEN SALDOAUX.CENTRO_COSTO IS NULL
                                   THEN ' ' 
                                   ELSE CENTRO_COSTO_NOMBRE
                                   END
                            ||CASE WHEN SALDOAUX.TERCERO IS NULL
                                   THEN ' '
                                   ELSE TERCERO_NOMBRE
                                   END
                            ||CASE WHEN SALDOAUX.AUXILIAR IS NULL
                                   THEN ' '
                                   ELSE AUXILIAR_NOMBRE
                                   END
                            || SALDOAUX.NOMBRE AS ORDENCUENTAS,
           CASE WHEN SUBSTR(SALDOAUX.CODIGO,1,1)='0'
             THEN 'z'||SALDOAUX.CODIGO
             ELSE SALDOAUX.CODIGO
             END AS ORDEN,

           SALDOAUX.CENTRO_COSTO,
           SALDOAUX.AUXILIAR,
           (CASE WHEN SALDOAUX.NATURALEZA='D' AND SALDOAUX.SALDO0 >=0
                 THEN SALDOAUX.SALDO0
                 ELSE 0
                 END  + CASE WHEN SALDOAUX.NATURALEZA='C' AND SALDOAUX.SALDO0 <0
                             THEN -SALDOAUX.SALDO0
                             ELSE 0
                             END) AS SALDOANTDEBITO,
       (CASE WHEN SALDOAUX.NATURALEZA='C' AND SALDOAUX.SALDO0 >=0
                  THEN SALDOAUX.SALDO0
                  ELSE 0
                  END  + CASE WHEN SALDOAUX.NATURALEZA='D'  AND SALDOAUX.SALDO0  <0
                             THEN -SALDOAUX.SALDO0
                             ELSE 0
                             END) AS SALDOANTCREDITO,
       (CASE WHEN SALDOAUX.NATURALEZA='D' AND SALDOAUX.SALDOs$mestrabajo$s >=0
                  THEN SALDOAUX.SALDOs$mestrabajo$s
                  ELSE 0
                  END  + CASE WHEN SALDOAUX.NATURALEZA='C' AND SALDOAUX.SALDOs$mestrabajo$s <0
                             THEN -SALDOAUX.SALDOs$mestrabajo$s
                             ELSE 0
                             END) AS SALDONUEDEBITO, 
            (CASE WHEN SALDOAUX.NATURALEZA='C' AND SALDOAUX.SALDOs$mestrabajo$s >=0
                  THEN SALDOAUX.SALDOs$mestrabajo$s
                  ELSE 0
                  END  + CASE WHEN SALDOAUX.NATURALEZA='D' AND SALDOAUX.SALDOs$mestrabajo$s  <0
                             THEN -SALDOAUX.SALDOs$mestrabajo$s
                             ELSE 0
                             END) AS SALDONUECREDITO
FROM (s$baseBalance$s) SALDOAUX
