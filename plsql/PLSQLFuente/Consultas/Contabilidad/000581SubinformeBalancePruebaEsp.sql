SELECT  SUBSTR(SALDOAUX.CODIGO,1,1)  CLASE,  
        SALDOAUX.CODIGO,  
        SALDOAUX.DEBITOs$mestrabajo$s       AS DEBITO ,  
        SALDOAUX.CREDITOs$mestrabajo$s      AS CREDITO,  
        SALDOAUX.NOMBRE, 
        SALDOAUX.COMPANIA,  
        SALDOAUX.DEBITOs$mestrabajo-1$s    AS DEBITO_1,  
        SALDOAUX.CREDITOs$mestrabajo-1$s   AS CREDITO_1 ,  
        SALDOAUX.SALDOs$mestrabajo$s       AS SALDO,  
        SALDOAUX.SALDOs$mestrabajo-1$s        AS SALDO_1,  
        SUBSTR((CASE WHEN SUBSTR(SALDOAUX.CODIGO,1,1)='0' 
             THEN 'z'||SALDOAUX.CODIGO 
             ELSE SALDOAUX.CODIGO 
             END),1,1) AS CLASEORDEN, 
        SALDOAUX.CODIGO||CASE WHEN SALDOAUX.CENTRO_COSTO IS NULL 
                                   THEN ' '  
                                   ELSE CENTRO_COSTO_NOMBRE 
                                   END 
                            ||CASE WHEN SALDOAUX.TERCERO  
IS NULL 
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
             END AS ORDEN 
FROM (s$baseBalance$s) SALDOAUX
