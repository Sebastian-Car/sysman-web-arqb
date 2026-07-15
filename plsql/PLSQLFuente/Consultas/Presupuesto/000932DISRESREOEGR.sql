WITH JERARQUIA AS (SELECT DCP.COMPANIA,
                          DCP.ANO,
                          DCP.TIPO_CPTE,
                          DCP.COMPROBANTE,
                          DCP.ANO_AFECT,
                          DCP.TIPO_CPTE_AFECT,
                          DCP.COMPROBANTE_AFECT,
                          LEVEL NIVEL,
                          TCPP.CLASE
                   FROM COMPROBANTE_PPTALAFECTADOS DCP
                   INNER JOIN TIPO_COMPROBPP TCPP 
                         ON (DCP.COMPANIA= TCPP.COMPANIA 
                         AND DCP.TIPO_CPTE=TCPP.CODIGO)
                   WHERE DCP.COMPANIA       = s$compania$s
                   START WITH DCP.ANO_AFECT  = s$anio$s
                     AND DCP.TIPO_CPTE_AFECT = 's$tipo$s'
                     AND DCP.COMPROBANTE_AFECT BETWEEN s$numeroInicial$s AND s$numeroFinal$s 
                   CONNECT BY PRIOR  DCP.COMPANIA = DCP.COMPANIA
                     AND  PRIOR DCP.ANO          = DCP.ANO_AFECT
                     AND  PRIOR DCP.TIPO_CPTE    = DCP.TIPO_CPTE_AFECT
                     AND  PRIOR DCP.COMPROBANTE  = DCP.COMPROBANTE_AFECT
                UNION ALL
                   SELECT CPP.COMPANIA,
                          CPP.ANO,
                          CPP.TIPO,
                          CPP.NUMERO,
                          DCP.ANO_AFECT,
                          DCP.TIPO_CPTE_AFECT,
                          DCP.COMPROBANTE_AFECT,
                          0,
                          TCPP.CLASE
                   FROM COMPROBANTE_PPTAL CPP
                   LEFT JOIN COMPROBANTE_PPTALAFECTADOS DCP 
                         ON (CPP.COMPANIA = DCP.COMPANIA 
                         AND CPP.ANO      = DCP.ANO 
                         AND CPP.TIPO     = DCP.TIPO_CPTE 
                         AND CPP.NUMERO   = DCP.COMPROBANTE)
                   INNER JOIN TIPO_COMPROBPP TCPP 
                           ON(CPP.COMPANIA = TCPP.COMPANIA 
                          AND CPP.TIPO     = TCPP.CODIGO)      
                   WHERE CPP.COMPANIA = s$compania$s
                     AND CPP.ANO      = s$anio$s
                     AND CPP.TIPO     = 's$tipo$s'
                     AND CPP.NUMERO BETWEEN s$numeroInicial$s AND s$numeroFinal$s 
                   ORDER BY NIVEL    
   ), RESUMEN AS(
         SELECT  DCPP.COMPANIA, 
             DCPP.ANO, 
             DCPP.TIPO_CPTE TIPO, 
             DCPP.COMPROBANTE,   
             DCPP.CONSECUTIVO,
             DCPP.FECHA,
             DCPP.ANO_AFECT, 
             DCPP.TIPO_CPTE_AFECT, 
             DCPP.CMPTE_AFECTADO, 
             DCPP.CONSECUTIVOPPTO,
             DCPP.VALOR_DEBITO - DCPP.VALOR_CREDITO + DCPP.MODIFICACION_DEBITO - DCPP.MODIFICACION_CREDITO AS VALOR,
             DCPP.CUENTA,
             TERCERO.NOMBRE TERCERO,
             JQ.CLASE      
         FROM JERARQUIA JQ 
         INNER JOIN DETALLE_COMPROBANTE_PPTAL DCPP 
         ON (JQ.COMPANIA = DCPP.COMPANIA 
            AND JQ.ANO= DCPP.ANO 
            AND JQ.TIPO_CPTE=DCPP.TIPO_CPTE 
            AND JQ.COMPROBANTE=DCPP.COMPROBANTE) 
        INNER JOIN TERCERO
         ON DCPP.COMPANIA = TERCERO.COMPANIA
         AND DCPP.SUCURSAL = TERCERO.SUCURSAL
         AND DCPP.TERCERO = TERCERO.NIT
   )
   SELECT 
       DIS.COMPANIA,  
       DIS.ANO            ANO_DIS,
       DIS.TIPO           TIPO_DIS,
       DIS.COMPROBANTE    DIS,
       DIS.FECHA          FECHA_DIS,
       DIS.TERCERO        BENEFICIARIO_DIS,
       DIS.CUENTA          CODIGO_CUENTA,
       SUM(DIS.VALOR)     VALOR_DIS, 
       RES.ANO            ANO_RES,
       RES.COMPROBANTE    RES, 
       RES.TIPO           TIPO_RES,
       RES.FECHA          FECHA_RES, 
       SUM(RES.VALOR)     VALOR_RES,
       RES.TERCERO        BENEFICIARIO_RES,
       REO.ANO            ANO_REO,
       REO.COMPROBANTE    REO, 
       REO.TIPO           TIPO_REO,
       REO.FECHA          FECHA_REO, 
       SUM(REO.VALOR)     VALOR_REO,
       REO.TERCERO        BENEFICIARIO_REO,           
       EGR.ANO            ANO_EGR,
       EGR.COMPROBANTE    EGR, 
       EGR.TIPO           TIPO_EGR,
       EGR.FECHA          FECHA_EGR, 
       SUM(EGR.VALOR)     VALOR_EGR,
       EGR.TERCERO        BENEFICIARIO_EGR
   FROM RESUMEN DIS
   LEFT JOIN RESUMEN RES 
         ON DIS.COMPANIA    = RES.COMPANIA
        AND DIS.ANO         = RES.ANO_AFECT
        AND DIS.TIPO        = RES.TIPO_CPTE_AFECT
        AND DIS.COMPROBANTE = RES.CMPTE_AFECTADO
        AND DIS.CONSECUTIVO = RES.CONSECUTIVOPPTO
  LEFT JOIN RESUMEN REO 
         ON RES.COMPANIA    = REO.COMPANIA
        AND RES.ANO         = REO.ANO_AFECT
        AND RES.TIPO        = REO.TIPO_CPTE_AFECT
        AND RES.COMPROBANTE = REO.CMPTE_AFECTADO
        AND RES.CONSECUTIVO = REO.CONSECUTIVOPPTO 
   LEFT JOIN RESUMEN EGR 
         ON REO.COMPANIA    = EGR.COMPANIA
        AND REO.ANO         = EGR.ANO_AFECT
        AND REO.TIPO        = EGR.TIPO_CPTE_AFECT
        AND REO.COMPROBANTE = EGR.CMPTE_AFECTADO
        AND REO.CONSECUTIVO = EGR.CONSECUTIVOPPTO
  WHERE DIS.CLASE            = 'DIS'         
  AND (RES.CLASE            = 'RES' OR  RES.CLASE IS NULL)
  AND (REO.CLASE            = 'REO' OR  REO.CLASE IS NULL)
  AND (EGR.CLASE            = 'EGR' OR  EGR.CLASE IS NULL)
  GROUP BY DIS.COMPANIA,  
       DIS.ANO            ,
       DIS.TIPO           ,
       DIS.COMPROBANTE    ,
       DIS.FECHA          ,
       DIS.TERCERO        ,
       DIS.CUENTA         ,
       RES.ANO            ,
       RES.COMPROBANTE    , 
       RES.TIPO           ,
       RES.FECHA          , 
       RES.TERCERO        ,
       REO.ANO            ,
       REO.COMPROBANTE    , 
       REO.TIPO           ,
       REO.FECHA          , 
       REO.TERCERO        ,           
       EGR.ANO            ,
       EGR.COMPROBANTE    , 
       EGR.TIPO          ,
       EGR.FECHA          , 
       EGR.TERCERO        
 ORDER BY DIS.COMPANIA, DIS.ANO, DIS.CUENTA
