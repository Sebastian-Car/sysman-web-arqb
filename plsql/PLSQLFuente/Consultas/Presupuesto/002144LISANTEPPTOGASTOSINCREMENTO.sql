SELECT
  Saldo_plan_Pptal.COMPANIA,
  Saldo_plan_Pptal.ANO,
  Saldo_plan_Pptal.CODIGO,
  plan_Presupuestal.CODIGO,
  plan_Presupuestal.NOMBRE,
  SUM(
    CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Apropiacion_Debito - Saldo_plan_Pptal.Apropiacion_Credito)
      ELSE (Saldo_plan_Pptal.Apropiacion_Credito - Saldo_plan_Pptal.Apropiacion_Debito)
    END) Apropiado,
  SUM(Saldo_plan_Pptal.ADICION)   AS Adi,
  SUM(Saldo_plan_Pptal.REDUCCION) AS Red,
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) Traslado,
  SUM(
    CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END) Aplazam,
  (SUM(Saldo_plan_Pptal.ADICION) + SUM(Saldo_plan_Pptal.REDUCCION) + 
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) +
    SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END) ) AS Modificaciones,
  (SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Apropiacion_Debito - Saldo_plan_Pptal.Apropiacion_Credito)
      ELSE (Saldo_plan_Pptal.Apropiacion_Credito - Saldo_plan_Pptal.Apropiacion_Debito)
    END) + SUM(Saldo_plan_Pptal.ADICION) + SUM(Saldo_plan_Pptal.REDUCCION) +
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) + 
    SUM( CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END)) AS AprDefinitiva,
  (SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Apropiacion_Debito - Saldo_plan_Pptal.Apropiacion_Credito)
      ELSE (Saldo_plan_Pptal.Apropiacion_Credito - Saldo_plan_Pptal.Apropiacion_Debito)
    END) + SUM(Saldo_plan_Pptal.ADICION) + SUM(Saldo_plan_Pptal.REDUCCION) +
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) +
    SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END)) *(1.s$xx$s) AS IncXX,
    
  (SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Apropiacion_Debito - Saldo_plan_Pptal.Apropiacion_Credito)
      ELSE (Saldo_plan_Pptal.Apropiacion_Credito - Saldo_plan_Pptal.Apropiacion_Debito)
    END) + SUM(Saldo_plan_Pptal.ADICION) + SUM(Saldo_plan_Pptal.REDUCCION) +
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) +SUM( CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END))*(1.s$yy$s) AS IncYY,
    
  (SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Apropiacion_Debito - Saldo_plan_Pptal.Apropiacion_Credito)
      ELSE (Saldo_plan_Pptal.Apropiacion_Credito - Saldo_plan_Pptal.Apropiacion_Debito)
    END) + SUM(Saldo_plan_Pptal.ADICION) + SUM(Saldo_plan_Pptal.REDUCCION) +
  SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Traslado_Debito - Saldo_plan_Pptal.Traslado_Credito)
      ELSE (Saldo_plan_Pptal.Traslado_Credito - Saldo_plan_Pptal.Traslado_Debito)
    END) +SUM(CASE WHEN plan_Presupuestal.NATURALEZA='D'
      THEN (Saldo_plan_Pptal.Aplazam_Debito  - Saldo_plan_Pptal.Aplazam_Credito)
      ELSE (Saldo_plan_Pptal.Aplazam_Credito - Saldo_plan_Pptal.Aplazam_Debito)
    END))*(1.s$zz$s) AS IncZZ
    
FROM
  plan_Presupuestal
LEFT JOIN Saldo_plan_Pptal
ON
  plan_Presupuestal.CODIGO         = Saldo_plan_Pptal.CODIGO
AND plan_Presupuestal.ANO      = Saldo_plan_Pptal.ANO
AND plan_Presupuestal.COMPANIA = Saldo_plan_Pptal.COMPANIA

WHERE 
    plan_Presupuestal.COMPANIA = s$compania$s
AND plan_Presupuestal.CODIGO   BETWEEN s$cuentaInicial$s AND s$cuentaFinal$s
AND plan_Presupuestal.ANO      = s$anio$s
AND Saldo_plan_Pptal.MES <=12
AND LENGTH(plan_Presupuestal.CODIGO)  <= s$digitos$s  

GROUP BY
  Saldo_plan_Pptal.COMPANIA,
  Saldo_plan_Pptal.ANO,
  Saldo_plan_Pptal.CODIGO,
  plan_Presupuestal.CODIGO,
  plan_Presupuestal.NOMBRE
 