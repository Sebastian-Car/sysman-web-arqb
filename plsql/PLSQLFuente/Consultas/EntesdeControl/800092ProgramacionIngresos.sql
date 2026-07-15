WITH RESUMENPPTO_P_I AS(
           SELECT SALDO_PLAN_PPTAL.COMPANIA,
                  SALDO_PLAN_PPTAL.ANO,
                  NVL(CODIGOSCHIP,' ')                                                           CODIGOEQUIVALENTE,
                  PLAN_PRESUPUESTAL.NATURALEZA,
                  SUM(APROPIACION_CREDITO - APROPIACION_DEBITO)   APROPIADO,
                  SUM(MODIF_PAC_CREDITO   - MODIF_PAC_DEBITO)        MODIFPAC,
                  SUM(EJE_CNT_CREDITO     - EJE_CNT_DEBITO)                EJECUCIONCNT,
                  SUM(TRASLADO_CREDITO    - TRASLADO_DEBITO)      TRASLADO,
                  SUM(EJE_PPT_CREDITO     - EJE_PPT_DEBITO)       EJECUCIONPPT,
                  SUM(APLAZAM_CREDITO     - APLAZAM_DEBITO)       APLAZAMIENTO,
                  SUM(SALDO_PLAN_PPTAL.ADICION)                   ADICION,
                  SUM(SALDO_PLAN_PPTAL.REDUCCION)                 REDUCCION,
                  SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD)            DISPONIBILIDAD,
                  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT)              REG_CONTRACT,
                  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT)           REG_NO_CONTRACT,
                  SUM(SALDO_PLAN_PPTAL.REG_REVERSION)             REG_REVERSION,
                  SUM(SALDO_PLAN_PPTAL.VIGENCIAANTERIOR)          VIGENCIAANTERIOR,
                  SUM(SALDO_PLAN_PPTAL.VIGENCIAFUTURA)            VIGENCIAFUTURA,
                  SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)             PAC_APROPIADO,
                  SUM(SALDO_PLAN_PPTAL.PAC_PROGRAMADO)            PAC_PROGRAMADO,
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)            MODIF_REG_CONT,
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)          MODIF_REG_NOCONT,
                  SUM(SALDO_PLAN_PPTAL.REINTEGRO)                 REINTEGRO,      
                  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT) 
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)          REGNOCONTRACT,
                  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT) 
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)            REGCONT,
                  SUM(SALDO_PLAN_PPTAL.PAC_APROPIADO)  
                  + 
                  SUM(MODIF_PAC_CREDITO - MODIF_PAC_DEBITO)       PACTOTAL,
                  SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION)       REO,
                  SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) MODIFREO,
                  SUM(SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION) 
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION) TOTALREO,
                  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS)            TOTALMODIFINGRESOS,  
                  SUM(EJE_PPT_CREDITO  - EJE_PPT_DEBITO) 
                  +
                  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS)            TOTALINGRESOS,
                  SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)         INGRESOSCAUSADOS,
                  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS)   MODIFICACIONICA,
                  SUM(SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS)  
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS)   TOTALICA,
                  SUM(SALDO_PLAN_PPTAL.PAC_EJECUTADO)             PACEJECUTADO_P,  
                  SUM(SALDO_PLAN_PPTAL.EJE_PPT_DEBITO)            CREDITOS,
                  SUM(SALDO_PLAN_PPTAL.EJE_PPT_CREDITO)           CONTRACREDITOS,
                  PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS,
                  PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS,
                  PLAN_PRESUPUESTAL.RECURSOSCHIP,  
                  SUM(SALDO_PLAN_PPTAL.REG_NO_CONTRACT) 
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_NOCONT)  
                  + 
                  SUM(SALDO_PLAN_PPTAL.REG_CONTRACT) 
                  + 
                  SUM(SALDO_PLAN_PPTAL.MODIF_REG_CONT)            REGISTROSP,
                  SUM(SALDO_PLAN_PPTAL.TRASLADO_DEBITO)           MOD_DEBITO,
                  SUM(SALDO_PLAN_PPTAL.TRASLADO_CREDITO)         MOD_CREDITO,
                  SUM(SALDO_PLAN_PPTAL.APLAZAM_DEBITO)              APLAZADEB,
                  SUM(SALDO_PLAN_PPTAL.APLAZAM_CREDITO)           APLAZACRE,
                  SUM(SALDO_PLAN_PPTAL.RECONOCIMIENTOS)           RECON,
                  PLAN_PRESUPUESTAL.COD_RECIPROCA,  
                  CASE WHEN SITUACIONFONDOSSCHIP = '0' THEN 'C' ELSE 'S' END  COD_SIT       
            FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
               LEFT JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL
                 ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA
                AND PLAN_PRESUPUESTAL.ID       = SALDO_PLAN_PPTAL.CODIGO 
                AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO
            WHERE SALDO_PLAN_PPTAL.COMPANIA     = s$compania$s
              AND SALDO_PLAN_PPTAL.ANO          = s$anio$s
              AND CODIGOSCHIP                   IS NOT NULL
              AND PLAN_PRESUPUESTAL.NATURALEZA  IN('C')     
              AND SALDO_PLAN_PPTAL.MES          <= s$mesFinal$s
            GROUP BY SALDO_PLAN_PPTAL.COMPANIA,
                  SALDO_PLAN_PPTAL.ANO,
                  NVL(CODIGOSCHIP,' '),
                  PLAN_PRESUPUESTAL.NATURALEZA,
                  PLAN_PRESUPUESTAL.ORIGENESPECIFICOINGRESOS,
                  PLAN_PRESUPUESTAL.DESTINACIONDELOSRECURSOS,
                  PLAN_PRESUPUESTAL.RECURSOSCHIP,
                  PLAN_PRESUPUESTAL.COD_RECIPROCA,
                  CASE WHEN SITUACIONFONDOSSCHIP = '0' THEN 'C' ELSE 'S' END
)
  SELECT  RESUMENPPTO_P_I.CODIGOEQUIVALENTE                       CONCEPTO,
         TO_CHAR(RESUMENPPTO_P_I.RECURSOSCHIP)                   COD_REC,
          RESUMENPPTO_P_I.ORIGENESPECIFICOINGRESOS                COD_OEI,
          RESUMENPPTO_P_I.DESTINACIONDELOSRECURSOS                COD_DEST_ESTT,
          CASE WHEN COD_SIT IN('C') THEN '1' ELSE '2' END         COD_SIT,
          s$indicadorActo$sMI_ACTOAD                              ACTO,
          ROUND(RESUMENPPTO_P_I.APROPIADO,0)                      PRE_INI_INGR,
          ROUND(RESUMENPPTO_P_I.ADICION,0)                        ADICION,
          ABS(ROUND(RESUMENPPTO_P_I.REDUCCION,0))                 REDUCCION,
          ABS(ROUND(RESUMENPPTO_P_I.MOD_DEBITO,0) )               CREDITOS,       
          ABS(ROUND(RESUMENPPTO_P_I.MOD_CREDITO,0))               CONTRACREDITOS, 
          ABS(ROUND(RESUMENPPTO_P_I.APLAZADEB,0))                 APLAZADEB,
          ABS(ROUND(RESUMENPPTO_P_I.APLAZACRE,0))                 APLAZACRE,
          0                                                       DEFINITIVO
   FROM RESUMENPPTO_P_I        
  WHERE RESUMENPPTO_P_I.RECURSOSCHIP                        IS NOT NULL
    AND RESUMENPPTO_P_I.ORIGENESPECIFICOINGRESOS            IS NOT NULL
    AND RESUMENPPTO_P_I.DESTINACIONDELOSRECURSOS            IS NOT NULL  
    AND(    TRUNC(RESUMENPPTO_P_I.APROPIADO + 0.501)        NOT IN (0) 
         OR TRUNC(RESUMENPPTO_P_I.ADICION + 0.501)          NOT IN (0)
         OR TRUNC(ABS(CASE WHEN RESUMENPPTO_P_I.REDUCCION   >= 0
                           THEN RESUMENPPTO_P_I.REDUCCION   +  0.501
                           ELSE RESUMENPPTO_P_I.REDUCCION   -  0.501
                      END )) NOT IN (0)
         OR TRUNC(ABS(CASE WHEN RESUMENPPTO_P_I.MOD_CREDITO >= 0
                           THEN RESUMENPPTO_P_I.MOD_CREDITO +  0.501
                           ELSE RESUMENPPTO_P_I.MOD_CREDITO -  0.501
                      END )) NOT IN (0)
         OR TRUNC(ABS(CASE WHEN RESUMENPPTO_P_I.MOD_DEBITO  >= 0
                           THEN RESUMENPPTO_P_I.MOD_DEBITO  +  0.501
                           ELSE RESUMENPPTO_P_I.MOD_DEBITO  -  0.501
                      END )) NOT IN (0)              
         OR TRUNC(ABS(CASE WHEN RESUMENPPTO_P_I.APLAZADEB   >= 0
                           THEN RESUMENPPTO_P_I.APLAZADEB   +  0.501
                           ELSE RESUMENPPTO_P_I.APLAZADEB   -  0.501
                      END )) NOT IN (0)
         OR TRUNC(ABS(CASE WHEN RESUMENPPTO_P_I.APLAZACRE   >= 0
                           THEN RESUMENPPTO_P_I.APLAZACRE   +  0.501
                           ELSE RESUMENPPTO_P_I.APLAZACRE   -  0.501
                      END )) NOT IN (0))
    ORDER BY CODIGOEQUIVALENTE  
