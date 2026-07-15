SELECT C.NITCOMPANIA                                                                                    "Nit Sujeto Vigilado",
       C.NOMBRE                                                                                         "Nombre Sujeto Vigilado",
       PCK_SYSMAN_UTL.FC_PAR(OC.COMPANIA,'REGIMEN F20A1',1,OC.FECHA)                                    "Regimen De Contratación",
       0                                                                                                "Presupuesto Del Sujeto Vigilado",
       PCK_SYSMAN_UTL.FC_PAR(OC.COMPANIA,'ORIGEN DEL PRESUPUESTO',1,OC.FECHA)                           "Origen Del Presupuesto",      
       D.NOMBRE                                                                                         "Departamento",
       CD.NOMBRE                                                                                        "Municipio",
       OC.NUMERO                                                                                        "Número Del Contrato", 
       TA.EQUIVALENTESIA                                                                                "Modalidad De Selección",
       TA.PROCEDIMIENTOSIA                                                                              "Procedimiento",
       TC.TIPOCONTRATO_SIA                                                                              "Clase De Contrato",
       'FUNCIONAMIENTO'                                                                                 "Tipo De Gasto",
       ST.SECTOR_SIA                                                                                    "Sector Al Que Corresponde El Gasto",
       OC.OBJETOCONTRATO                                                                                "Objeto Del Contrato",   
       OC.VALORTOTAL                                                                                    " Valor Inicial Del Contrato",
       OC.CEDULACONTRATISTA                                                                             "Cédula / Nit Del Contratista",
       OC.NOMBRECONTRATISTA                                                                             "Nombre  Del Contratista" ,
       OC.TIPOCONTRATISTA                                                                               "Persona Natural O Jurídica",
       CASE WHEN OC.FECHASUSCRIPCION IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHASUSCRIPCION,'YYYY/MM/DD') END                                               "Fecha De Suscripción",
       CASE WHEN OC.CEDULAINTERVENTOR IS NULL
          THEN  SUP.CEDULA
          ELSE OC.CEDULAINTERVENTOR END                                                                 "Cédula / Nit Del Interventor",
       CASE WHEN OC.INTERVENTOR  IS NULL 
          THEN  TER.NOMBRE
          ELSE OC.INTERVENTOR END                                                                       " Nombre  Interventor",
       CASE WHEN TIPOINTERVENTOR='I'
       THEN 'INTERNO'
       ELSE 'EXTERNO' END                                                                               "Tipo Vinculación Interventor",
       'DIAS'                                                                                           "Plazo De Ejecución - Unidad", 
       OC.PLAZODEENTREGA                                                                                "Plazo De Ejecución - Número",
       CASE WHEN OC.ABONOS=0
        THEN 'NO'
        ELSE 'SI' END                                                                                   "Pacto Anticipo Al Contrato ",
       OC.ABONOS                                                                                        "Valores De Los Anticipos",
       'ND'                                                                                             "Constituyo Fiducia Mercantil",
       CASE WHEN OC.FECHAINICIO IS NULL
       THEN TO_CHAR(OC.FECHA,'YYYY/MM/DD')
       ELSE TO_CHAR(OC.FECHAINICIO,'YYYY/MM/DD') END                                                    "Fecha Inicio Del Contrato",
       OC.FECHAFINALIZACION                                                                             "Fecha Terminación Del Contrato",
       CASE WHEN OC.FECHAPUBLICACION IS NULL
            THEN   'NO'
            ELSE   'SI' END                                                                             "Publicación En El Secop",
       URG.NUMEROACTO                                                                                   "Número Del Acto Que La Decreta",
       NVL(TO_CHAR(URG.FECHAACTO,'YYYY/MM/DD'),'ND')                                                    "Fecha Del Acto ",
       NVL(RECURSOS.VALOR_RECURSOS_PROPIOS,0)                                                           "Recursos Propios ",
       NVL(RECURSOS.VALOR_RECURSOS_REGALIAS,0)                                                          "Regalias ",
       NVL(RECURSOS.VALOR_RECURSOS_SGP,0)                                                               "Sgp ", 
       NVL(RECURSOS.VALOR_RECURSOS_COLHUMANITARIA,0)                                                    "Fnc - Colombia Humanitaria",
       NVL(VIGENCIA_FUTURA.FECHA_AUTO_VF,'ND')                                                          "Fecha De Autorización VF ",
       NVL(VIGENCIA_FUTURA.ANO_INICIAL,'ND')                                                            " Vf. Autorizada Año Inicia ",
       NVL(VIGENCIA_FUTURA.ANO_FINAL,'ND')                                                              "Vf. Autorizada Año Final ",
      0                                                                                                 " Monto Total De La Vf Autorizado",
      0                                                                                                 "Monto De La Vf Apropiado ",
      0                                                                                                 " Monto De La Vf Ejecutado ",
      0                                                                                                 "Saldo Total De La Vf Por Comprometer"
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
 
   LEFT JOIN SUPERVISORES SUP
            ON OC.COMPANIA=SUP.COMPANIA
            AND OC.CLASEORDEN=SUP.CLASEORDEN
            AND OC.NUMERO=SUP.NUMEROCONTRATO
   LEFT JOIN TERCERO TER     
            ON  SUP.COMPANIA=TER.COMPANIA
            AND SUP.CEDULA=TER.NIT
            AND SUP.SUCURSAL=TER.SUCURSAL 
   LEFT JOIN URGENCIAMANIFIESTA URG
          ON  OC.COMPANIA   =URG.COMPANIA
          AND OC.CLASEORDEN =URG.CLASEORDEN
          AND OC.NUMERO     =URG.ORDENDECOMPRA
   LEFT JOIN(SELECT  V_DETALLE_AUXILIAR_PPTAL.COMPANIA,
                      V_DETALLE_AUXILIAR_PPTAL.TIPOCONTRATO,
                      V_DETALLE_AUXILIAR_PPTAL.NUMEROCONTRATO,
                      SUM(CASE WHEN FUENTE_RECURSOS.CODIGO_SIA='001'
                           THEN V_DETALLE_AUXILIAR_PPTAL.VALOR_DEBITO
                               - V_DETALLE_AUXILIAR_PPTAL.VALOR_CREDITO
                           ELSE 0 END) VALOR_RECURSOS_PROPIOS,
                     SUM(CASE WHEN FUENTE_RECURSOS.CODIGO_SIA='002'
                           THEN V_DETALLE_AUXILIAR_PPTAL.VALOR_DEBITO
                               - V_DETALLE_AUXILIAR_PPTAL.VALOR_CREDITO
                           ELSE 0 END) VALOR_RECURSOS_SGP,
                    SUM(CASE WHEN FUENTE_RECURSOS.CODIGO_SIA='003'
                           THEN V_DETALLE_AUXILIAR_PPTAL.VALOR_DEBITO
                               - V_DETALLE_AUXILIAR_PPTAL.VALOR_CREDITO
                           ELSE 0 END) VALOR_RECURSOS_REGALIAS,
                   SUM(CASE WHEN FUENTE_RECURSOS.CODIGO_SIA='006'
                           THEN V_DETALLE_AUXILIAR_PPTAL.VALOR_DEBITO
                               - V_DETALLE_AUXILIAR_PPTAL.VALOR_CREDITO
                           ELSE 0 END) VALOR_RECURSOS_COLHUMANITARIA
              FROM V_DETALLE_AUXILIAR_PPTAL INNER JOIN FUENTE_RECURSOS
                     ON   V_DETALLE_AUXILIAR_PPTAL.COMPANIA=FUENTE_RECURSOS.COMPANIA
                     AND  V_DETALLE_AUXILIAR_PPTAL.ANO=FUENTE_RECURSOS.ANO
                     AND  V_DETALLE_AUXILIAR_PPTAL.FUENTE_RECURSO_DET=FUENTE_RECURSOS.CODIGO
                   LEFT JOIN   ORDENDECOMPRA 
                     ON   V_DETALLE_AUXILIAR_PPTAL.COMPANIA=ORDENDECOMPRA.COMPANIA
                     AND  V_DETALLE_AUXILIAR_PPTAL.TIPOCONTRATO=ORDENDECOMPRA.CLASEORDEN
                     AND  V_DETALLE_AUXILIAR_PPTAL.NUMEROCONTRATO=ORDENDECOMPRA.NUMERO
                  INNER JOIN TIPO_COMPROBPP TIPOCPTE
                     ON   V_DETALLE_AUXILIAR_PPTAL.COMPANIA=TIPOCPTE.COMPANIA
                     AND  V_DETALLE_AUXILIAR_PPTAL.TIPO=TIPOCPTE.CODIGO
             WHERE V_DETALLE_AUXILIAR_PPTAL.COMPANIA=s$compania$s
                 AND V_DETALLE_AUXILIAR_PPTAL.ANO=s$ano$s
                 AND V_DETALLE_AUXILIAR_PPTAL.MES BETWEEN s$mesInicial$s  AND s$mesFinal$s
                 AND TIPOCPTE.CLASE IN('RES','ADR','DMR') 
              GROUP BY V_DETALLE_AUXILIAR_PPTAL.COMPANIA,
                       V_DETALLE_AUXILIAR_PPTAL.TIPOCONTRATO,
                       V_DETALLE_AUXILIAR_PPTAL.NUMEROCONTRATO) RECURSOS         
        ON  OC.COMPANIA   =RECURSOS.COMPANIA
        AND OC.CLASEORDEN =RECURSOS.TIPOCONTRATO
        AND OC.NUMERO     =RECURSOS.NUMEROCONTRATO  
  LEFT JOIN (SELECT  V_DETALLE_AUXILIAR_PPTAL.COMPANIA,
        V_DETALLE_AUXILIAR_PPTAL.TIPOCONTRATO,
        V_DETALLE_AUXILIAR_PPTAL.NUMEROCONTRATO,
        TO_CHAR(FECHA_AUTO_VF,'YYYY/MM/DD') FECHA_AUTO_VF,
        TO_CHAR(FECHA_AUTO_VF,'YYYY')  ANO_INICIAL,
        TO_CHAR(COMPROBANTE_PPTAL.FECHA_APROB_VF,'YYYY') ANO_FINAL
        
            FROM V_DETALLE_AUXILIAR_PPTAL LEFT JOIN   ORDENDECOMPRA 
                 ON   V_DETALLE_AUXILIAR_PPTAL.COMPANIA=ORDENDECOMPRA.COMPANIA
                 AND  V_DETALLE_AUXILIAR_PPTAL.TIPOCONTRATO=ORDENDECOMPRA.CLASEORDEN
                 AND  V_DETALLE_AUXILIAR_PPTAL.NUMEROCONTRATO=ORDENDECOMPRA.NUMERO
              INNER JOIN TIPO_COMPROBPP TIPOCPTE
                 ON   V_DETALLE_AUXILIAR_PPTAL.COMPANIA=TIPOCPTE.COMPANIA
                 AND  V_DETALLE_AUXILIAR_PPTAL.TIPO=TIPOCPTE.CODIGO
             INNER JOIN PLAN_PRESUPUESTAL
                    ON  V_DETALLE_AUXILIAR_PPTAL.COMPANIA=PLAN_PRESUPUESTAL.COMPANIA
                    AND V_DETALLE_AUXILIAR_PPTAL.ANO=PLAN_PRESUPUESTAL.ANO
                    AND V_DETALLE_AUXILIAR_PPTAL.CUENTA=PLAN_PRESUPUESTAL.CODIGO
              INNER JOIN COMPROBANTE_PPTAL
                    ON  V_DETALLE_AUXILIAR_PPTAL.COMPANIA=COMPROBANTE_PPTAL.COMPANIA
                    AND V_DETALLE_AUXILIAR_PPTAL.ANO=COMPROBANTE_PPTAL.ANO
                    AND V_DETALLE_AUXILIAR_PPTAL.TIPO=COMPROBANTE_PPTAL.TIPO
                    AND V_DETALLE_AUXILIAR_PPTAL.COMPROBANTE=COMPROBANTE_PPTAL.NUMERO
             WHERE V_DETALLE_AUXILIAR_PPTAL.COMPANIA=s$compania$s
                 AND V_DETALLE_AUXILIAR_PPTAL.ANO=s$ano$s
                 AND V_DETALLE_AUXILIAR_PPTAL.MES BETWEEN s$mesInicial$s  AND s$mesFinal$s
                 AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN ('VF')
                 AND TIPOCPTE.CLASE IN('RES','ADR','DMR'))VIGENCIA_FUTURA          
       ON  OC.COMPANIA   =VIGENCIA_FUTURA.COMPANIA
        AND OC.CLASEORDEN =VIGENCIA_FUTURA.TIPOCONTRATO
        AND OC.NUMERO     =VIGENCIA_FUTURA.NUMEROCONTRATO     
          
 WHERE   OC.COMPANIA=s$compania$s
       AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
       AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s  AND s$mesFinal$s
