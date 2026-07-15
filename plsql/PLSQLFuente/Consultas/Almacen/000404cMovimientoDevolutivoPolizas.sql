MERGE INTO CONSULTAS FIN
USING (
          SELECT
              '000404cMovimientoDevolutivoPolizas'                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       INFORME,
              TO_CLOB(q'[SELECT
  D_MOVIMIENTO.COMPANIA,
  D_MOVIMIENTO.TIPOMOVIMIENTO,  
  D_MOVIMIENTO.MOVIMIENTO,
  D_MOVIMIENTO.ELEMENTO, 
  D_MOVIMIENTO.SERIE, 
  TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY') FECHA,
  TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS') HORA, 
  D_MOVIMIENTO.IND_REG, 
  MOVIMIENTO.RESPONSABLE_DESTINO RESPON,  
  MOVIMIENTO.DEPENDENCIA_DESTINO DEPEN,
  MOVIMIENTO.DESCRIPCION,
  D_MOVIMIENTO.VALORUNITARIO, 
  TIPOMOVIMIENTO.NOMBRE,
  TIPOMOVIMIENTO.TIPOELEMENTO,  
  D_MOVIMIENTO.VALORUNITARIO, 
  D_MOVIMIENTO.ELEMENTO ELEM,   
  TIPOMOVIMIENTO.CLASE,  
  TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY') EXP,
  D_POLIZAS_ACTIVOS.ASEGURADORA,  
  MAX(D_POLIZAS_ACTIVOS.NUMERO_POLIZA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NUMERO_POLIZA,  
  TO_CHAR(MAX(D_POLIZAS_ACTIVOS.FECHAI) KEEP (DENSE_RANK LAST ORDER BY ROWNUM),'DD/MM/YYYY') FECHAI, 
  TO_CHAR(MAX(D_POLIZAS_ACTIVOS.FECHAF) KEEP (DENSE_RANK LAST ORDER BY ROWNUM),'DD/MM/YYYY') FECHAF, 
  ASEGURADORA.NOMBRE NOMBRE_ASEGURADORA 
FROM DEVOLUTIVO
  INNER JOIN D_MOVIMIENTO   
     ON DEVOLUTIVO.COMPANIA           = D_MOVIMIENTO.COMPANIA   
    AND DEVOLUTIVO.SERIE              = D_MOVIMIENTO.SERIE    
    AND DEVOLUTIVO.ELEMENTO           = D_MOVIMIENTO.ELEMENTO 
  INNER JOIN MOVIMIENTO  
     ON D_MOVIMIENTO.COMPANIA         = MOVIMIENTO.COMPANIA
    AND D_MOVIMIENTO.MOVIMIENTO       = MOVIMIENTO.NUMERO
    AND D_MOVIMIENTO.TIPOMOVIMIENTO   = MOVIMIENTO.TIPOMOVIMIENTO
  INNER JOIN TIPOMOVIMIENTO  
     ON D_MOVIMIENTO.COMPANIA         = TIPOMOVIMIENTO.COMPANIA  
    AND D_MOVIMIENTO.TIPOMOVIMIENTO   = TIPOMOVIMIENTO.CODIGO 
  LEFT JOIN D_POLIZAS_ACTIVOS
     ON DEVOLUTIVO.COMPANIA           = D_POLIZAS_ACTIVOS.COMPANIA
    AND DEVOLUTIVO.SERIE              = D_POLIZAS_ACTIVOS.SERIE  
    AND DEVOLUTIVO.ELEMENTO           = D_POLIZAS_ACTIVOS.ELEMENTO
  LEFT JOIN ASEGURADORA
     ON D_POLIZAS_ACTIVOS.ASEGURADORA = ASEGURADORA.NITASEGURADORA    
WHERE D_MOVIMIENTO.COMPANIA = s$compania$s
  AND D_MOVIMIENTO.SERIE = s$Placa$s
  AND TIPOMOVIMIENTO.TIPOELEMENTO<>'C'
GROUP BY 
  D_MOVIMIENTO.COMPANIA, D_MOVIMIENTO.TIPOMOVIMIENTO, D_MOVIMIENTO.MOVIMIENTO, D_MOVIMIENTO.ELEMENTO, D_MOVIMIENTO.SERIE, TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY'),
  TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS'),   D_MOVIMIENTO.IND_REG, MOVIMIENTO.RESPONSABLE_DESTINO, MOVIMIENTO.DEPENDENCIA_DESTINO, MOVIMIENTO.DESCRIPCION, D_MOVIMIENTO.VALORUNITARIO,
  TIPOMOVIMIENTO.NOMBRE, TIPOMOVIMIENTO.TIPOELEMENTO,   D_MOVIMIENTO.VALORUNITARIO, D_MOVIMIENTO.ELEMENTO, TIPOMOVIMIENTO.CLASE, D_POLIZAS_ACTIVOS.ASEGURADORA, 
  ASEGURADORA.NOMBRE,
  D_MOVIMIENTO.FECHA
ORDER BY 
  D_MOVIMIENTO.COMPANIA,
  TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS')]')     CONSULTA,
              10                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         APLICACION,
              TO_CLOB(q'[]')                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             CONSULTA_OPCIONAL,
              NULL                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       CREATED_BY,
              NULL                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       MODIFIED_BY
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