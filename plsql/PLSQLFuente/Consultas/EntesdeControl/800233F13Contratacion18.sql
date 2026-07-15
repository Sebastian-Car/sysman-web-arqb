SELECT C.NITCOMPANIA                                                                                    "Nit Sujeto Vigilado",
       C.NOMBRE                                                                                         "Nombre Sujeto Vigilado",
       0                                                                                                "Presupuesto Del Sujeto Vigilado",
       C.DIRECCION                                                                                      "Direccion Del Sujeto Vigilado",
       OC.NUMERO                                                                                        "Número Del Contrato", 
       TA.EQUIVALENTESIA                                                                                "Modalidad De Selección",
       TC.TIPOCONTRATO_SIA                                                                              "Clase De Contrato",
       'FUNCIONAMIENTO'                                                                                 "Tipo De Gasto",
       ST.SECTOR_SIA                                                                                    "Fuente Del Recurso",
       OC.OBJETOCONTRATO                                                                                "Objeto Del Contrato",   
       OC.VALORTOTAL                                                                                    "Valor Inicial Del Contrato",
       OC.CEDULACONTRATISTA                                                                             "Cédula / Nit Del Contratista",
       OC.NOMBRECONTRATISTA                                                                             "Nombre  Del Contratista",
       CASE WHEN OC.FECHASUSCRIPCION IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHASUSCRIPCION,'YYYY/MM/DD') END                                               "Fecha De Suscripción ",
       DIS.TIPO||'-'||DIS.COMPROBANTES                                                                  "Número De Cdp",                                                                     
       DIS.FECHACDP                                                                                     "Fecha De Cdp", 
       OCR.RUBRONOMBRE                                                                                  " Rubro Del Cdp",
       NVL(DIS.TOTAL_DIS,0)                                                                             "Valor De Cdp",
       RES.TIPO||'-'||RES.COMPROBANTES                                                                  "Número De Rp",                                                                     
       NVL(RES.FECHACDP,'ND')                                                                           "Fecha De Rp", 
       OCR.RUBRONOMBRE                                                                                  "Rubro Del Rp",
       NVL(RES.TOTAL_RES,0)                                                                             "Valor De Rp",
       NVL(CASE WHEN OC.CEDULAINTERVENTOR IS NULL
          THEN  SUP.CEDULA
          ELSE OC.CEDULAINTERVENTOR END,'ND')                                                           " Nit Del Interventor",
       NVL(CASE WHEN OC.INTERVENTOR  IS NULL 
          THEN  TER.NOMBRE
          ELSE OC.INTERVENTOR END,'ND')                                                                " Nombre Interventor",
       CASE WHEN TIPOINTERVENTOR='I'
       THEN 'INTERNO'
       ELSE 'EXTERNO' END                                                                               "(C) Tipo Vinculación Interventor",
       'DIAS'                                                                                           "Plazo Ejecución - Unidad", 
       OC.PLAZODEENTREGA                                                                                "Plazo  Ejecución-Número",
       CASE WHEN OC.ABONOS=0
        THEN 'NO'
        ELSE 'SI' END                                                                                   "Pacto Anticipo Al Contrato ",
        NVL(ADICIONES.ADICION,0)                                                                        "Valor Total De Las Adiciones",
        0                                                                                               "(C) Prorrogas-Unidad",
        0                                                                                               "(N) Prorrogas-Número",
        NVL(POL.TIPO,'ND')                                                                              "Clase De Garantía",
        NVL(POL.CODIGO,0)                                                                               "Poliza Número",
        NVL(TO_CHAR(POL.VIGENCIADESDE,'DD/MM/YYYY'),'ND')                                               "Fecha Inicial De La Póliza",
        NVL(TO_CHAR(POL.VIGENCIAHASTA,'DD/MM/YYYY'),'ND')                                               "Fecha Final De La Poliza",
        NVL(ASEGURADORA.NOMBRE,'ND')                                                                    "Nombre De La Aseguradora",
       CASE WHEN OC.VALORPAGOS=0
         THEN  PAGO_CONTRATO.VALOR_PAGOS
         ELSE OC.VALORPAGOS 
         END                                                                                             "Pagos Efectuados",
     CASE WHEN OC.FECHAINICIO IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHAINICIO,'YYYY/MM/DD') END                                                    " Fecha Inicio Del Contrato",
       OC.FECHAFINALIZACION                                                                             " Fecha Terminación  Contrato",
       OC.FECHALIQUIDACION                                                                              "Fecha  Acta Liquidación"
FROM ORDENDECOMPRA OC 
      INNER JOIN COMPANIA C
            ON OC.COMPANIA=C.CODIGO
      LEFT JOIN TIPOADJUDICACION TA
          ON OC.COMPANIA=TA.COMPANIA
          AND OC.TIPOADJUDICACION=TA.CODIGO
      INNER JOIN TIPOORDENDECOMPRA TC
           ON OC.COMPANIA=TC.COMPANIA
           AND OC.CLASEORDEN=TC.CODIGO
      LEFT JOIN SECTORES ST
           ON OC.COMPANIA=ST.COMPANIA
           AND OC.SECTOR=ST.CODIGO
      LEFT JOIN DEPENDENCIA DE
          ON OC.COMPANIA=DE.COMPANIA
          AND OC.DEPENDENCIA=DE.CODIGO
      LEFT JOIN V_ORDENDECOMPRA_DIS DIS
          ON  OC.COMPANIA=DIS.COMPANIA
          AND OC.CLASEORDEN=DIS.CLASEORDEN
          AND OC.NUMERO=DIS.NUMERO
      LEFT JOIN (SELECT N.COMPANIA,
                    N.CLASEORDEN,
                    N.ORDENDECOMPRA,
                    SUM(N.VALORTOTAL) ADICION 
              FROM NOVEDADCONTRATO N
              WHERE N.COMPANIA=s$compania$s
                   AND  N.ANO= s$ano$s
                   AND  TO_CHAR(N.FECHA,'MM')<= s$mesFinal$s
 		   AND N.TIPOT IN('ADC','AD1')            
              GROUP BY N.CLASEORDEN,
                       N.ORDENDECOMPRA,
                       N.COMPANIA) ADICIONES
       ON OC.COMPANIA=ADICIONES.COMPANIA
         AND  OC.CLASEORDEN=ADICIONES.CLASEORDEN
         AND  OC.NUMERO=ADICIONES.ORDENDECOMPRA 
      LEFT JOIN (SELECT CN.COMPANIA,
                    CN.TIPOCONTRATO,
                    CN.NUMEROCONTRATO,
                    SUM(CN.VLR_DOCUMENTO)VALOR_PAGOS 
              FROM COMPROBANTE_CNT CN
              WHERE CN.COMPANIA=s$compania$s
                   AND  CN.ANO= s$ano$s
                   AND  TO_CHAR(CN.FECHA,'MM')<=s$mesFinal$s        
              GROUP BY  CN.COMPANIA,
                        CN.TIPOCONTRATO,
                        CN.NUMEROCONTRATO) PAGO_CONTRATO
       ON OC.COMPANIA=PAGO_CONTRATO.COMPANIA
         AND  OC.CLASEORDEN=PAGO_CONTRATO.TIPOCONTRATO
         AND  OC.NUMERO=PAGO_CONTRATO.NUMEROCONTRATO      
    LEFT JOIN V_ORDENDECOMPRA_DIS DIS
           ON OC.COMPANIA=DIS.COMPANIA
           AND OC.CLASEORDEN=DIS.CLASEORDEN
           AND OC.NUMERO=DIS.NUMERO
   LEFT JOIN V_ORDENDECOMPRA_RUBRO OCR
            ON OC.COMPANIA=OCR.COMPANIA
            AND OC.CLASEORDEN=OCR.CLASEORDEN
            AND OC.NUMERO=OCR.NUMERO 
  LEFT JOIN V_ORDENDECOMPRA_RES RES
           ON OC.COMPANIA=RES.COMPANIA
           AND OC.CLASEORDEN=RES.CLASEORDEN
           AND OC.NUMERO=RES.NUMERO
   LEFT JOIN SUPERVISORES SUP
            ON OC.COMPANIA=SUP.COMPANIA
            AND OC.CLASEORDEN=SUP.CLASEORDEN
            AND OC.NUMERO=SUP.NUMEROCONTRATO
   LEFT JOIN TERCERO TER     
            ON  SUP.COMPANIA=TER.COMPANIA
            AND SUP.CEDULA=TER.NIT
            AND SUP.SUCURSAL=TER.SUCURSAL 
  LEFT JOIN POLIZAS POL
            ON  OC.COMPANIA=POL.COMPANIA
            AND OC.CLASEORDEN=POL.CLASEORDEN
            AND OC.NUMERO=POL.ORDENDECOMPRA
   LEFT JOIN ASEGURADORA
             ON ASEGURADORA.COMPANIA=POL.COMPANIA
             AND ASEGURADORA.NITASEGURADORA=POL.ASEGURADORA
             AND ASEGURADORA.SUCURSAL=POL.SUCURSAL  
 WHERE   OC.COMPANIA=s$compania$s
       AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
       AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s  AND s$mesFinal$s   
