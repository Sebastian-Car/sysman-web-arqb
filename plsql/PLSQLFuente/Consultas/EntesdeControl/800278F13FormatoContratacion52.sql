SELECT OC.NUMERO                                                                                        "Número Del Contrato", 
       OC.REGISTROBPI                                                                                   "Código Banco Proyectos",
       'ND'                                                                                             "Linea o Estrategia",
       ST.SECTOR_SIA                                                                                    "Fuente Del Recurso",
       OC.OBJETOCONTRATO                                                                                "Objeto Del Contrato",
       TC.TIPOCONTRATO_SIA                                                                              "Clase De Contrato",
       OC.VALORTOTAL                                                                                    "Valor Del Contrato",
       OC.CEDULACONTRATISTA                                                                             "Nit Del Contratista",
       OC.NOMBRECONTRATISTA                                                                             "Nombre Del Contratista",
       DIS.TIPO||'-'||DIS.COMPROBANTES                                                                  "Número Disponibilidad",                                                                     
       DIS.FECHACDP                                                                                     "Fecha Disponibilidad", 
       NVL(DIS.TOTAL_DIS,0)                                                                             "Valor Disponibilidad",
       CASE WHEN OC.FECHAFIRMA IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHAFIRMA,'YYYY/MM/DD') END                                                     "Fecha Firma Contrato",
       TA.EQUIVALENTESIA                                                                                "Forma Contratacion",
       NVL(RES.FECHACDP,'ND')                                                                           "Fecha Registro", 
       NVL(RES.TOTAL_RES,0)                                                                             "Valor Registro",
       OCR.RUBRONOMBRE                                                                                  "Rubro Registro",
       CASE WHEN (OC.CEDULAINTERVENTOR IS NULL AND SUP.CEDULA IS NULL) 
          THEN  'NO'
          ELSE 'SI' END                                                                                  "Asignó Interventor",      
       NVL(CASE WHEN OC.CEDULAINTERVENTOR IS NULL
          THEN  SUP.CEDULA
          ELSE OC.CEDULAINTERVENTOR END,'ND')                                                           "Nit Del Interventor",
       NVL(CASE WHEN OC.INTERVENTOR  IS NULL 
          THEN  TER.NOMBRE
          ELSE OC.INTERVENTOR END,'ND')                                                                "Nombre Interventor",
       CASE WHEN TIPOINTERVENTOR='I'
       THEN 'INTERNO'
       ELSE 'EXTERNO' END                                                                              "Vinculación Interventor",
       NVL(TO_CHAR(POL.VIGENCIADESDE,'DD/MM/YYYY'),'ND')                                               "Fecha Aprobación Garantia",
       CASE WHEN OC.FECHAINICIO IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHAINICIO,'YYYY/MM/DD') END                                                    "Fecha Inicio Contrato",
       OC.PLAZODEENTREGA                                                                                "Plazo Ejecución-Número",
       'DIAS'                                                                                           "Plazo Ejecución-Und", 
       CASE WHEN OC.ABONOS=0
        THEN 'NO'
        ELSE 'SI' END                                                                                   "Pacto Anticipo",
        OC.VALOR_ANTICIPO                                                                               "Valor Anticipo ",
        'ND'                                                                                            "Fecha Anticipo",
        NVL(ADICIONES.FECHANOV,'ND')                                                                    "Fecha Adición",
        0                                                                                               "Plazo Adición",
        NVL(ADICIONES.ADICION,0)                                                                        "Valor Total Adiciones",
        CASE WHEN OC.VALORPAGOS=0
         THEN  PAGO_CONTRATO.VALOR_PAGOS
         ELSE OC.VALORPAGOS 
         END                                                                                             "Pagos Efectuados",
        OC.FECHAFINALIZACION                                                                             "Fecha Terminación",
       OC.FECHALIQUIDACION                                                                              "Fecha De Liquidación"
FROM ORDENDECOMPRA OC 
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
                    TO_CHAR(N.FECHA,'DD/MM/YYYY')FECHANOV,
                    SUM(N.VALORTOTAL) ADICION 
              FROM NOVEDADCONTRATO N
              WHERE N.COMPANIA=s$compania$s
                   AND  N.ANO= s$ano$s
                   AND  TO_CHAR(N.FECHA,'MM')<= s$mesFinal$s
 		   AND N.TIPOT IN('ADC','AD1')            
              GROUP BY N.CLASEORDEN,
                       N.ORDENDECOMPRA,
                       N.COMPANIA,
                       N.FECHA) ADICIONES
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
