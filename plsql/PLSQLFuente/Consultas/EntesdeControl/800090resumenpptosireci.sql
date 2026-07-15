
WITH ResumenPpto_sireci AS(

SELECT SALDO_PLAN_PPTAL.COMPANIA,
  SALDO_PLAN_PPTAL.ANO,
  PLAN_PRESUPUESTAL.CODIGOSIRECI AS CODIGOEQUIVALENTE,
  PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS,
  PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS,
  PLAN_PRESUPUESTAL.RECURSOSCHIP,
  PLAN_PRESUPUESTAL.FINALIDADGASTO,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN APROPIACION_DEBITO - APROPIACION_CREDITO
    ELSE APROPIACION_CREDITO-APROPIACION_DEBITO
  END ) AS APROPIADOO,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN MODIF_PAC_DEBITO - MODIF_PAC_CREDITO
    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO
  END ) AS MODIFPAC,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN EJE_CNT_DEBITO - EJE_CNT_CREDITO
    ELSE EJE_CNT_CREDITO-EJE_CNT_DEBITO
  END ) AS EJECUCIONCNT,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN TRASLADO_DEBITO - TRASLADO_CREDITO
    ELSE TRASLADO_CREDITO-TRASLADO_DEBITO
  END) AS TRASLADO,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN EJE_PPT_DEBITO - EJE_PPT_CREDITO
    ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
  END) AS EJECUCIONPPT,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN APLAZAM_DEBITO - APLAZAM_CREDITO
    ELSE APLAZAM_CREDITO-APLAZAM_DEBITO
  END)                          AS APLAZAMIENTO,
  SUM(SALDO_PLAN_PPTAL.ADICION) AS ADICIONN,
  SUM(ABS(REDUCCION))           AS REDUCCIONN,
  (SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN APROPIACION_DEBITO - APROPIACION_CREDITO
    ELSE APROPIACION_CREDITO-APROPIACION_DEBITO
  END )                     +SUM(SALDO_PLAN_PPTAL.ADICION)-SUM(ABS(REDUCCION))+SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN TRASLADO_DEBITO - TRASLADO_CREDITO
    ELSE TRASLADO_CREDITO-TRASLADO_DEBITO
  END)                   +SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN APLAZAM_DEBITO - APLAZAM_CREDITO
    ELSE APLAZAM_CREDITO-APLAZAM_DEBITO
  END))                                                                                                                                                                                       AS TOTALAPROPIADOO,
  SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD)                                                                                                                                                        AS DISPONIBILIDAD,
  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT)                                                                                                                                                          AS REG_CONTRAC,
  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)                                                                                                                                                       AS REG_NO_CONTRAC,
  SUM(SALDO_PLAN_PPTAL.REG_REVERSION)                                                                                                                                                         AS REG_REVERSION,
  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)                                                                                                                                                       AS REGNOCONT,
  SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)                                                                                                                                                        AS MODREGCONT,
  SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)                                                                                                                                                      AS MODREGNOCONT,
  SUM(EJE_PPT_DEBITO                 -EJE_PPT_CREDITO)                                                                                                                                        AS PAGOSACUMULADOS,
  SUM(REGISTRO_OBLIGACION            +MODIF_REGISTRO_OBLIGACION)                                                                                                                              AS REGISTROOBLIGACION,
  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT) +SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT) +SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT) AS RESERVAS,
  SUM(SALDO_PLAN_PPTAL.VIGENCIAANTERIOR)                                                                                                                                                      AS VIGENCIAANTERIOR,
  SUM(SALDO_PLAN_PPTAL.VIGENCIAFUTURA)                                                                                                                                                        AS VIGENCIAFUTURA,
  SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)                                                                                                                                                         AS PAC_APROPIADO,
  SUM(SALDO_PLAN_PPTAL.PAC_PROGRAMADO)                                                                                                                                                        AS PAC_PROGRAMADO,
  SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)                                                                                                                                                        AS MODIF_REG_CON,
  SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)                                                                                                                                                      AS MODIF_REG_NOCON,
  SUM(SALDO_PLAN_PPTAL.REINTEGRO)                                                                                                                                                             AS REINTEGRO,
  PLAN_PRESUPUESTAL.NATURALEZA,
  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT) AS REGNOCONTRACT,
  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT)   +SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)   AS REGCONT,
  SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)                        +SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN MODIF_PAC_DEBITO - MODIF_PAC_CREDITO
    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO
  END )                                                                                     AS PACTOTAL,
  SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION)                                                 AS REO,
  SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION)                                           AS MODIFREO,
  SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION)+SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) AS TOTALREO,
  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS)                                                      AS TOTALMODIFINGRESOS,
  SUM(
  CASE
    WHEN PLAN_PRESUPUESTAL.NATURALEZA ='D'
    THEN EJE_PPT_DEBITO - EJE_PPT_CREDITO
    ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO
  END)                  +SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS)                           AS TOTALINGRESOS,
  SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)                                               AS INGRESOSCAUSADOS,
  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS)                                         AS MODIFICACIONICA,
  SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)+SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS) AS TOTALICA,
  SUM(SALDO_PLAN_PPTAL.PAC_EJECUTADO)                                                   AS PACEJECUTADO_P,
  SUM(TRASLADO_DEBITO +APLAZAM_DEBITO)                                                  AS CREDITOSS,
  SUM(TRASLADO_CREDITO+APLAZAM_CREDITO)                                                 AS CONTRACREDITOSS,
  PLAN_PRESUPUESTAL.VIGENCIAGASTO,
  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)+SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)+SUM(SALDO_PLAN_PPTAL.REG_CONTRACT) +SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT) AS REGISTROSP,
  SUM(SALDO_PLAN_PPTAL.TRASLADO_DEBITO)                                                                                                                 AS SUMADETRASLADO_DEBITO,
  SUM(SALDO_PLAN_PPTAL.TRASLADO_CREDITO)                                                                                                                AS SUMADETRASLADO_CREDITO,
  PLAN_PRESUPUESTAL.DEPENDENCIASCHIP
FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
INNER JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL
ON (PLAN_PRESUPUESTAL.COMPANIA      = SALDO_PLAN_PPTAL.COMPANIA)
AND (PLAN_PRESUPUESTAL.ANO          = SALDO_PLAN_PPTAL.ANO)
AND (PLAN_PRESUPUESTAL.ID           = SALDO_PLAN_PPTAL.ID)
WHERE SALDO_PLAN_PPTAL.COMPANIA     ='001'
AND SALDO_PLAN_PPTAL.ANO            =2011
--AND PLAN_PRESUPUESTAL.CODIGOSIRECI IS NOT NULL
--AND PLAN_PRESUPUESTAL.NATURALEZA    ='D'
GROUP BY Saldo_plan_pptal.COMPANIA,
  Saldo_plan_pptal.ANO,
  Plan_presupuestal.CODIGOSIRECI,
  Plan_presupuestal.DESTINACIONDELOSRECURSOS,
  Plan_presupuestal.ORIGENESPECIFICOINGRESOS,
  Plan_presupuestal.RECURSOSCHIP,
  Plan_presupuestal.FINALIDADGASTO,
  Plan_presupuestal.NATURALEZA,
  Plan_presupuestal.VIGENCIAGASTO,
  Plan_presupuestal.DEPENDENCIASCHIP) 
  
SELECT CodigoEquivalente                                                                                                                                                            AS Concepto,
  VigenciaGasto                                                                                                                                                                     AS VIG_GAST,
  CASE WHEN VigenciaGasto = '1' THEN APROPIADOO      ELSE 0  END AS APROPIADO,
  CASE WHEN VigenciaGasto = '1' THEN ADICIONN        ELSE 0  END AS ADICION,
  CASE WHEN VigenciaGasto = '1' THEN REDUCCIONN      ELSE 0  END AS REDUCCION,
  CASE WHEN VigenciaGasto = '1' THEN CREDITOSS       ELSE 0  END AS CREDITOS,
  CASE WHEN VigenciaGasto = '1' THEN CONTRACREDITOSS ELSE 0  END AS CONTRACREDITOS,
  CASE WHEN VigenciaGasto = '1' THEN TOTALAPROPIADOO ELSE 0  END AS TOTALAPROPIADO,
  CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.RegistrosP >0 THEN ResumenPpto_Sireci.RegistrosP  +0.501 ELSE ResumenPpto_Sireci.RegistrosP -0.501 END) END  AS COMPROMISOS,
  CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.TotalREO > 0 THEN ResumenPpto_Sireci.TotalREO     + 0.501 ELSE ResumenPpto_Sireci.TotalREO -0.501 END ) END     AS OBLIGACIONES,
  CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.TotalIngresos>0 THEN ResumenPpto_Sireci.TotalIngresos +0.501 ELSE  ResumenPpto_Sireci.TotalIngresos-0.50 END ) END AS PAGOS,
  CASE WHEN VigenciaGasto = '2' THEN TotalApropiadoo ELSE 0 END AS RESERVASCONSTITUIDAS,
  CASE WHEN VigenciaGasto = '3' THEN TOTALAPROPIADOO ELSE 0 END  AS CUENTASXPAGARCONSTITUIDAS,
  CASE WHEN VigenciaGasto ='2' THEN CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.TotalREO > 0 THEN ResumenPpto_Sireci.TotalREO     + 0.501 ELSE ResumenPpto_Sireci.TotalREO -0.501 END ) END ELSE 0 END  AS RESERVASPRESUPUEST,
  CASE WHEN VigenciaGasto ='2' THEN CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.TotalIngresos>0 THEN ResumenPpto_Sireci.TotalIngresos +0.501 ELSE  ResumenPpto_Sireci.TotalIngresos-0.50 END ) END ELSE  0 END AS RESERVASPRESUPUESTALESPAGOS,
  CASE WHEN VigenciaGasto ='3' THEN CASE WHEN  NVL(VigenciaGasto,'') ='1' THEN TRUNC(CASE WHEN ResumenPpto_Sireci.TotalIngresos>0 THEN ResumenPpto_Sireci.TotalIngresos +0.501 ELSE  ResumenPpto_Sireci.TotalIngresos-0.50 END ) END ELSE 0 END  AS CUENTASxPAGARPAGOS
FROM ResumenPpto_sireci 
WHERE ResumenPpto_Sireci.NATURALEZA                 ='D'
