SELECT C.NITCOMPANIA                                           NIT_ENTIDAD,
       C.NOMBRE                                                NOMBRE_ENTIDAD,
       CASE WHEN OC.CONVENIO=0
       THEN 'LEY 80' 
       ELSE 'CONVENIOS LEY 489' END                           REGIMEN_DE_CONTRATACION,
       0                                                      PRESUPUESTO_ENTIDAD,
       'NACIONAL'                                             ORIGEN_DEL_PRESUPUESTO,
       D.NOMBRE                                               DEPARTAMENTO,
       CD.NOMBRE                                              MUNICIPIO,
       OC.NUMERO                                              NUMERO_DEL_CONTRATO, 
       TA.EQUIVALENTESIA                                      MODALIDAD_DE_SELECCION,
       TA.PROCEDIMIENTOSIA                                    PROCEDIMIENTO,
       TC.NOMBRE                                              CLASE_CONTRATO,
       'FUNCIONAMIENTO'                                       TIPO_DE_GASTO, 
       ST.SECTOR_SIA                                          SECTOR_CORREPONDE_GASTO,
       OC.OBJETOCONTRATO                                      OBJETO_DEL_CONTRATO,
       OC.VALORTOTAL                                          VALOR_INICIAL,
       OC.CEDULACONTRATISTA                                   CEDULA_NIT_CONTRATISTA,
       OC.NOMBRECONTRATISTA                                   NOMBRE_COMPLETO,
       OC.TIPOCONTRATISTA                                     PERSONA_NATURAL_JURIDICA,
       D.NOMBRE                                               DEPENDENCIA, 
       CASE WHEN OC.FECHASUSCRIPCION IS NULL
       THEN TO_CHAR(OC.FECHA,'DD/MM/YYYY')
       ELSE TO_CHAR(OC.FECHASUSCRIPCION,'DD/MM/YYYY') END     FECHA_DE_SUSCRIPCION,
       OC.CEDULAINTERVENTOR                                   CEDULA_INTERVENTOR_SUPERVISOR,
       OC.INTERVENTOR                                         NOMBRE_COMPLETO_INTERVENTOR,
       CASE WHEN TIPOINTERVENTOR='I'
       THEN 'INTERNO'
       ELSE 'EXTERNO' END                                     TIPOVINVULACION_INTERVENTOR,
       'DIAS'                                                 UNIDAD_DE_EJECUCION, 
       OC.PLAZODEENTREGA                                      NUMERO_DE_UNIDADES,
       CASE WHEN OC.FECHAINICIO IS NULL
       THEN TO_CHAR(OC.FECHA,'DD/MM/YYYY')
       ELSE TO_CHAR(OC.FECHAINICIO,'DD/MM/YYYY') END          FECHA_INICIO_CONTRATO,
       OC.FECHAFINALIZACION                                   FECHA_TERMINACION_CONTRATO,
       'ND'                                                   SE_PUBLICO_EN_SECOP,
       0                                                      VALOR_RECURSOS_PROPIOS,
       0                                                      VALOR_RECURSOS_REGALIAS,  
       0                                                      VALOR_RECURSOS_SGP,
       0                                                      FNC_COLOMBIA_HUMANITARIA,
       CASE WHEN NVL(ADICIONES.ADICION,0)>0
       THEN 'SI'
       ELSE 'N0' END                                          PRORROGAS,
      ADICIONES.ADICION                                       VALOR_TOTAL_ADICIONES,
      OC.FECHALIQUIDACION                                     FECHA_ACTA_LIQUIDACION,
      'ND'                                                    SE_ACTUALIZO_EN_SECOP,
      PAGO_CONTRATO.VALOR_PAGOS                               VALOR_PAGOS_PERIODO,
     DIS.COMPROBANTES                                        NO_DISP_PRESUPUESTAL,
     DIS.FECHACDP                                            FECHA_DISP_PRESUPUESTAL,
     DIS.TOTAL_DIS                                           VALOR_DISPONIBILIDAD,
     OCR.RUBRO                                               CODIGO_RUBRO_PRESUPUESTAL                                              
FROM ORDENDECOMPRA OC 
      INNER JOIN COMPANIA C
            ON OC.COMPANIA=C.CODIGO
      INNER JOIN DEPARTAMENTO D
          ON C.PAIS=D.PAIS
          AND  C.DEPARTAMENTO=D.CODIGO
      INNER JOIN CIUDAD CD
          ON  C.PAIS=CD.PAIS
          AND  C.DEPARTAMENTO=CD.DEPARTAMENTO
          AND  C.CIUDAD=CD.CODIGO
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
     WHERE   OC.COMPANIA=s$compania$s
              AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
              AND  TO_CHAR(OC.FECHA,'MM') BETWEEN s$mesInicial$s  AND s$mesFinal$s   
