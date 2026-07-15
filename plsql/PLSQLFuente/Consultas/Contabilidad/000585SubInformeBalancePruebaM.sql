SELECT  SUBSTR(PLAN_CONTABLE.CODIGO,1,1)  CLASE, 
        PLAN_CONTABLE.CODIGO, 
        PLAN_CONTABLE.DEBITOs$mestrabajo$s       AS DEBITO , 
        PLAN_CONTABLE.CREDITOs$mestrabajo$s      AS CREDITO, 
        PLAN_CONTABLE.NOMBRE,
        PLAN_CONTABLE.COMPANIA, 
        PLAN_CONTABLE.TERCERO,
        PLAN_CONTABLE.DEBITOs$mestrabajo-1$s    AS DEBITO_1, 
        PLAN_CONTABLE.CREDITOs$mestrabajo-1$s  AS CREDITO_1 , 
        PLAN_CONTABLE.SALDOs$mestrabajo$s         AS SALDO, 
        PLAN_CONTABLE.SALDOs$mestrabajo-1$s      AS SALDO_1, 
        SUBSTR((CASE WHEN SUBSTR(PLAN_CONTABLE.CODIGO,1,1)='0'
             THEN 'z'||PLAN_CONTABLE.CODIGO
             ELSE PLAN_CONTABLE.CODIGO
             END),1,1) AS CLASEORDEN,
        PLAN_CONTABLE.CODIGO||CASE WHEN PLAN_CONTABLE.CENTRO_COSTO IS NULL
                                   THEN ' ' 
                                   ELSE CENTRO_COSTO.NOMBRE
                                   END
                            ||CASE WHEN PLAN_CONTABLE.TERCERO IS NULL
                                   THEN ' '
                                   ELSE TERCERO.NOMBRE
                                   END
                            ||CASE WHEN PLAN_CONTABLE.AUXILIAR IS NULL
                                   THEN ' '
                                   ELSE AUXILIAR.NOMBRE
                                   END
                            || PLAN_CONTABLE.NOMBRE AS ORDENCUENTAS,
           CASE WHEN SUBSTR(PLAN_CONTABLE.CODIGO,1,1)='0'
             THEN 'z'||PLAN_CONTABLE.CODIGO
             ELSE PLAN_CONTABLE.CODIGO
             END AS ORDEN,
           PLAN_CONTABLE.TERCERO||TERCERO.DIGITOVERIFICACION AS NITCOMPLETO,
           PLAN_CONTABLE.CENTRO_COSTO,
           PLAN_CONTABLE.AUXILIAR,
           (CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mestrabajo-1$s >=0
                 THEN PLAN_CONTABLE.SALDOs$mestrabajo-1$s
                 ELSE 0
                 END  + CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mestrabajo-1$s <0
                             THEN -PLAN_CONTABLE.SALDOs$mestrabajo-1$s
                             ELSE 0
                             END) AS SALDOANTDEBITO,
       (CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mestrabajo-1$s >=0
                  THEN PLAN_CONTABLE.SALDOs$mestrabajo-1$s
                  ELSE 0
                  END  + CASE WHEN PLAN_CONTABLE.NATURALEZA='D'  AND PLAN_CONTABLE.SALDOs$mestrabajo-1$s  <0
                             THEN -PLAN_CONTABLE.SALDOs$mestrabajo-1$s
                             ELSE 0
                             END) AS SALDOANTCREDITO,
       (CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mestrabajo$s >=0
                  THEN PLAN_CONTABLE.SALDOs$mestrabajo$s
                  ELSE 0
                  END  + CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mestrabajo$s <0
                             THEN -PLAN_CONTABLE.SALDOs$mestrabajo$s
                             ELSE 0
                             END) AS SALDONUEDEBITO, 
            (CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mestrabajo$s >=0
                  THEN PLAN_CONTABLE.SALDOs$mestrabajo$s
                  ELSE 0
                  END  + CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mestrabajo$s  <0
                             THEN -PLAN_CONTABLE.SALDOs$mestrabajo$s
                             ELSE 0
                             END) AS SALDONUECREDITO     
FROM V_PLAN_CONTABLE PLAN_CONTABLE 
    LEFT JOIN TERCERO 
        ON  PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA 
        AND PLAN_CONTABLE.TERCERO  = TERCERO.NIT 
        AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL 
      LEFT JOIN AUXILIAR 
        ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA 
        AND PLAN_CONTABLE.ANO          = AUXILIAR.ANO   
        AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO 
    LEFT JOIN CENTRO_COSTO 
        ON  PLAN_CONTABLE.COMPANIA     = CENTRO_COSTO.COMPANIA    
        AND PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO       
        AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO   
WHERE PLAN_CONTABLE.COMPANIA       = s$compania$s 
     AND PLAN_CONTABLE.ANO            =  s$anoTrabajo$s  
     AND PLAN_CONTABLE.CODIGO    BETWEEN 's$codigoInicial$s' 
                                                                    AND 's$codigoFinal$s'       
  AND LENGTH(PLAN_CONTABLE.CODIGO) = 1
  s$condAuxiliar$s
  s$condCentroCosto$s
  s$condTercero$s 
  s$condSaldoCero$s 
  ORDER BY 
    PLAN_CONTABLE.COMPANIA, 
    PLAN_CONTABLE.ANO, 
    PLAN_CONTABLE.CODIGO
