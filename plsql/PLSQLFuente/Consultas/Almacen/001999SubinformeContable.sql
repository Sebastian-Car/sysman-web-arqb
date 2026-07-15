MERGE INTO CONSULTAS FIN
USING (
          SELECT
              '001999SubinformeContable'                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           INFORME,
              TO_CLOB(q'[SELECT DISTINCT 
 SUBSTR(D_MOVIMIENTO.ELEMENTO,0,s$nivelGrupo$s) GRUPO  
 ,ALMACENCONTABILIDAD.CUENTADEBITO CUENTA 
 ,DEBITO.NOMBRE NOMBRECUENTA
 ,SUM(D_MOVIMIENTO.VALORTOTAL) DEBITO 
 ,0 CREDITO
FROM D_MOVIMIENTO 
LEFT JOIN ALMACENCONTABILIDAD
       ON D_MOVIMIENTO.COMPANIA                 = ALMACENCONTABILIDAD.COMPANIA 
      AND SUBSTR(D_MOVIMIENTO.ELEMENTO,0,5)     = ALMACENCONTABILIDAD.CODIGOELEMENTO    
      AND D_MOVIMIENTO.TIPOMOVIMIENTO           = ALMACENCONTABILIDAD.TIPOMOVIMIENTO      
      AND EXTRACT(YEAR FROM D_MOVIMIENTO.FECHA) = ALMACENCONTABILIDAD.ANO      
LEFT JOIN PLAN_CONTABLE DEBITO
       ON DEBITO.COMPANIA = ALMACENCONTABILIDAD.COMPANIA
      AND DEBITO.ANO      = ALMACENCONTABILIDAD.ANO 
      AND DEBITO.CODIGO   = ALMACENCONTABILIDAD.CUENTADEBITO    
WHERE D_MOVIMIENTO.COMPANIA     = s$compania$s
AND D_MOVIMIENTO.TIPOMOVIMIENTO = '$P!{PR_TIPOMOVIMIENTO}'
AND D_MOVIMIENTO.MOVIMIENTO     = '$P!{PR_MOVIMIENTO}'
GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO,
  D_MOVIMIENTO.MOVIMIENTO,
  SUBSTR(D_MOVIMIENTO.ELEMENTO,0,s$nivelGrupo$s)
  ,ALMACENCONTABILIDAD.CUENTADEBITO
  ,ALMACENCONTABILIDAD.CUENTACREDITO 
  ,DEBITO.NOMBRE 
UNION ALL 
SELECT DISTINCT 
 SUBSTR(D_MOVIMIENTO.ELEMENTO,0,s$nivelGrupo$s) GRUPO  
 ,ALMACENCONTABILIDAD.CUENTACREDITO CUENTA
 ,CREDITO.NOMBRE NOMBRECUENTA
 ,0 DEBITO
 ,SUM(D_MOVIMIENTO.VALORTOTAL) CREDITO 
FROM D_MOVIMIENTO 
LEFT JOIN ALMACENCONTABILIDAD
       ON D_MOVIMIENTO.COMPANIA                 = ALMACENCONTABILIDAD.COMPANIA 
      AND SUBSTR(D_MOVIMIENTO.ELEMENTO,0,5)     = ALMACENCONTABILIDAD.CODIGOELEMENTO    
      AND D_MOVIMIENTO.TIPOMOVIMIENTO           = ALMACENCONTABILIDAD.TIPOMOVIMIENTO      
      AND EXTRACT(YEAR FROM D_MOVIMIENTO.FECHA) = ALMACENCONTABILIDAD.ANO       
LEFT JOIN PLAN_CONTABLE CREDITO
       ON CREDITO.COMPANIA = ALMACENCONTABILIDAD.COMPANIA
      AND CREDITO.ANO      = ALMACENCONTABILIDAD.ANO 
      AND CREDITO.CODIGO   = ALMACENCONTABILIDAD.CUENTACREDITO       
WHERE D_MOVIMIENTO.COMPANIA     = s$compania$s
AND D_MOVIMIENTO.TIPOMOVIMIENTO = '$P!{PR_TIPOMOVIMIENTO}'
AND D_MOVIMIENTO.MOVIMIENTO     = '$P!{PR_MOVIMIENTO}'
GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO,
  D_MOVIMIENTO.MOVIMIENTO,
  SUBSTR(D_MOVIMIENTO.ELEMENTO,0,s$nivelGrupo$s)
  ,ALMACENCONTABILIDAD.CUENTADEBITO
  ,ALMACENCONTABILIDAD.CUENTACREDITO 
  ,CREDITO.NOMBRE 
ORDER BY GRUPO  ]')     CONSULTA,
              10                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   APLICACION,
              TO_CLOB(q'[]')                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       CONSULTA_OPCIONAL,
              NULL                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 CREATED_BY,
              NULL                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 MODIFIED_BY
          FROM
              DUAL
      )
INI ON ( INI.INFORME = FIN.INFORME )
WHEN MATCHED THEN UPDATE
SET FIN.CONSULTA = INI.CONSULTA,
    FIN.APLICACION = INI.APLICACION,
    FIN.CONSULTA_OPCIONAL = INI.CONSULTA_OPCIONAL,
    FIN.MODIFIED_BY = INI.MODIFIED_BY,
    FIN.DATE_MODIFIED = SYSDATE
WHEN NOT MATCHED THEN
INSERT (
    INFORME,
    CONSULTA,
    APLICACION,
    CONSULTA_OPCIONAL,
    CREATED_BY,
    DATE_CREATED )
VALUES
    ( INI.INFORME,
      INI.CONSULTA,
      INI.APLICACION,
      INI.CONSULTA_OPCIONAL,
      INI.CREATED_BY,
      SYSDATE );

MERGE INTO CONSULTAS_SUB FIN USING (SELECT '000404cMovimientoDevolutivoPolizas' INFORME_PADRE, '001999SubinformeContable' INFORME_HIJO, 'PR_STRSQL_SUBCONTABLE' PARAMETRO ,NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (FIN.INFORME_PADRE = INI.INFORME_PADRE AND INI.INFORME_HIJO = FIN.INFORME_HIJO)  WHEN MATCHED THEN  UPDATE SET FIN.PARAMETRO = INI.PARAMETRO , FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME_PADRE,INFORME_HIJO, PARAMETRO,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME_PADRE,INI.INFORME_HIJO, INI.PARAMETRO,INI.CREATED_BY,SYSDATE);