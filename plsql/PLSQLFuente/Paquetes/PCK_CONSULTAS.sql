create or replace PACKAGE PCK_CONSULTAS AS 
--Consulta de CGR se quita la validacción de la naturaleza en los select pues filtra por la naturaleza
RESUMENPPTO_P_I    CONSTANT  CLOB := 'WITH INICIAL AS(
                                      SELECT PLAN.COMPANIA,
                                             PLAN.ANO,  
                                             SALDO.CODIGOSCHIP CODIGOEQUIVALENTE,
                                             SALDO.RECAUDO_VA,
                                             SALDO.NATURALEZA,
                                             SALDO.ORIGENESPECIFICOINGRESOS,
                                             SALDO.DESTINACIONDELOSRECURSOS,
                                             SALDO.RECURSOSCHIP,
                                             SALDO.COD_RECIPROCA,
                                             CASE WHEN SALDO.SITUACIONFONDOSSCHIP IN(0) THEN ''C'' ELSE ''S'' END AS COD_SIT,
                                             SUM(APROPIACION_CREDITO - APROPIACION_DEBITO ) APROPIADO,
                                             SUM(MODIF_PAC_CREDITO   - MODIF_PAC_DEBITO   ) MODIFPAC,
                                             SUM(EJE_CNT_CREDITO     - EJE_CNT_DEBITO     ) EJECUCIONCNT,
                                             SUM(TRASLADO_CREDITO    - TRASLADO_DEBITO    ) TRASLADO,
                                             SUM(EJE_PPT_CREDITO     - EJE_PPT_DEBITO     ) EJECUCIONPPT, 
                                             SUM(APLAZAM_CREDITO     - APLAZAM_DEBITO     ) APLAZAMIENTO,
                                             SUM(SALDO.ADICION                   ) ADICION, 
                                             SUM(SALDO.REDUCCION                 ) REDUCCION, 
                                             SUM(SALDO.DISPONIBILIDAD            ) DISPONIBILIDAD, 
                                             SUM(SALDO.DISPONIBILIDADADD         ) DISPONIBILIDAD_ADICION,  
                                             SUM(SALDO.DISPONIBILIDADDMD         ) DISPONIBILIDAD_DISMINUCION, 
                                             SUM(SALDO.REG_CONTRACT              ) REG_CONTRACT,       
                                             SUM(SALDO.REG_NO_CONTRACT           ) REG_NO_CONTRACT,    
                                             SUM(SALDO.REG_REVERSION             ) REG_REVERSION, 
                                             SUM(SALDO.VIGENCIAANTERIOR          ) VIGENCIAANTERIOR, 
                                             SUM(SALDO.VIGENCIAFUTURA            ) VIGENCIAFUTURA, 
                                             SUM(SALDO.PAC_APROPIADO             ) PAC_APROPIADO, 
                                             SUM(SALDO.PAC_COMPROMETIDO          ) PAC_COMPROMETIDO, 
                                             SUM(SALDO.PAC_PROGRAMADO            ) PAC_PROGRAMADO, 
                                             SUM(SALDO.MODIF_REG_CONT            ) MODIF_REG_CONT, 
                                             SUM(SALDO.MODIF_REG_CONTADR         ) MODIF_REG_CONT_ADICION,
                                             SUM(SALDO.MODIF_REG_CONTDMR         ) MODIF_REG_CONT_DISMINUCION,
                                             SUM(SALDO.MODIF_REG_NOCONT          ) MODIF_REG_NOCONT, 
                                             SUM(SALDO.MODIF_REG_NOCONTADR       ) MODIF_REG_NOCONT_ADICION, 	
                                             SUM(SALDO.MODIF_REG_NOCONTDMR       ) MODIF_REG_NOCONT_DISMINUCION,
                                             SUM(SALDO.REINTEGRO                 ) REINTEGRO, 
                                             SUM(SALDO.REGISTRO_OBLIGACION       ) REO,                
                                             SUM(SALDO.MODIF_REGISTRO_OBLIGACION ) MODIFREO,                   
                                             SUM(SALDO.MODIF_REGISTRO_OBLIGACIONARO) MODIFREO_ADICION,
                                             SUM(SALDO.MODIF_REGISTRO_OBLIGACIONDRO) MODIFREO_DISMINUCION,
                                             SUM(SALDO.MODIF_INGRESOS            ) TOTALMODIFINGRESOS, 
                                             SUM(SALDO.INGRESOS_CAUSADOS         ) INGRESOSCAUSADOS, 
                                             SUM(SALDO.MODIF_INGRESOS_CAUSADOS   ) MODIFICACIONICA, 
                                             SUM(SALDO.PAC_EJECUTADO             ) PACEJECUTADO,    
                                             SUM(SALDO.PACTESORERIA              ) PACTESORERIA,    
                                             SUM(SALDO.INGRESOS_EFECTIVO         ) INGRESOS_EFECTIVO,
                                             SUM(SALDO.INGRESOS_PAPELES          ) INGRESOS_PAPELES,
                                             SUM(SALDO.MODIF_INGRESOS_EFECTIVO   ) MODIF_INGRESOS_EFECTIVO,
                                             SUM(SALDO.MODIF_INGRESOS_PAPELES    ) MODIF_INGRESOS_PAPELES,
                                             SUM(SALDO.TRASLADO_DEBITO           ) MOD_DEBITO, 
                                             SUM(SALDO.APLAZAM_CREDITO           ) APLAZACRE, 
                                             SUM(SALDO.TRASLADO_CREDITO          ) MOD_CREDITO,
                                             SUM(SALDO.APLAZAM_DEBITO            ) APLAZADEB,  
                                             SUM(SALDO.RECONOCIMIENTOS           ) RECON
                                       FROM PLAN_PRESUPUESTAL PLAN INNER JOIN  SALDO_AUX_PPTAL SALDO
                                        ON PLAN.COMPANIA  = SALDO.COMPANIA
                                       AND PLAN.ANO       = SALDO.ANO
                                       AND PLAN.CODIGO    = SALDO.CODIGO
                                       WHERE PLAN.COMPANIA   =  ''s$compania$s''
                                         AND PLAN.ANO        =  s$anio$s
                                         AND PLAN.NATURALEZA =  ''C''
                                         AND SALDO.MES      <=  s$mesfinal$s 
                                         AND SALDO.CODIGOSCHIP IS NOT NULL   
                                       GROUP BY PLAN.COMPANIA,
                                             PLAN.ANO,  
                                             SALDO.CODIGOSCHIP,
                                             SALDO.RECAUDO_VA,
                                             SALDO.NATURALEZA,
                                             SALDO.ORIGENESPECIFICOINGRESOS,
                                             SALDO.DESTINACIONDELOSRECURSOS,
                                             SALDO.RECURSOSCHIP,
                                             SALDO.COD_RECIPROCA,
                                             CASE WHEN SALDO.SITUACIONFONDOSSCHIP IN(0) THEN ''C'' ELSE ''S'' END 
                                       )
                                       SELECT INICIAL.*,
                                               REO                 + MODIFREO                             REO, 
                                               EJECUCIONPPT        + TOTALMODIFINGRESOS                   TOTALINGRESOS, 
                                               APROPIADO + ADICION + REDUCCION + TRASLADO                 APRDEFINITIVA, 
                                               APROPIADO + ADICION + REDUCCION + TRASLADO + APLAZAMIENTO  APROPIACIONVIGENTE, 
                                               REG_NO_CONTRACT     + MODIF_REG_NOCONT                     REGNOCONTRACT, 
                                               REG_CONTRACT        + MODIF_REG_CONT                       REGCONT, 
                                               PAC_APROPIADO       + MODIFPAC                             PACTOTAL,          
                                               INGRESOSCAUSADOS    + MODIFICACIONICA                      TOTALICA,
                                               PACTESORERIA        - EJECUCIONCNT                         SALDOPACTESORERIA,
                                               REG_NO_CONTRACT     + MODIF_REG_NOCONT     + REG_CONTRACT 
                                                                   + MODIF_REG_CONT                       REGISTROSP
                                       FROM INICIAL';

  -- 1
  FUNCTION FC_RESUELVECONSULTA
  (
    UN_CONSULTA   IN CLOB
   ,UN_REEMPLAZOS IN PCK_SUBTIPOS.TI_CLAVEVALOR 
  )
    RETURN CLOB;

END PCK_CONSULTAS;