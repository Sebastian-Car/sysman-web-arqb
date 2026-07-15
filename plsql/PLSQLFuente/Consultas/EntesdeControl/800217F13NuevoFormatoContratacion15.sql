MERGE INTO CONSULTAS FIN USING (SELECT '800217F13NuevoFormatoContratacion15' INFORME ,TO_CLOB(q'[SELECT DISTINCT 
    OC.NUMERO                                                                                        "Número Del Contrato",
    LISTAGG(FREC.NOMBRE, ', ') WITHIN GROUP  (ORDER BY FREC.NOMBRE) over (partition by OC.NUMERO)    "Fuente Del Recurso",
    OC.OBJETOCONTRATO                                                                                "Objeto Del Contrato",
    TC.CODCONTTOLIMA                                                                                 "Clase De Contrato",
    OC.VALORTOTAL                                                                                    " Valor Inicial Del Contrato",
    OC.NOMBRECONTRATISTA                                                                             "Nombre Contratista",
    OC.CEDULACONTRATISTA                                                                             " Nit Contratista",
    DIS.TIPO||'-'||DIS.COMPROBANTES                                                                  "NÃºmero De Cdp",
    DIS.FECHACDP                                                                                     "Fecha De Cdp",
    NVL(DIS.TOTAL_DIS,0)                                                                             " Valor De Cdp",
    CASE WHEN OC.FECHASUSCRIPCION IS NULL
    THEN TO_CHAR(OC.FECHA,'DD/MM/YYYY')
    ELSE TO_CHAR(OC.FECHAFIRMA,'DD/MM/YYYY') END                                                     "Fecha Firma Contrato",
    TA.EQUIVALENTESIA                                                                                "Modalidad De SelecciÃ³n",
    NVL(RES.FECHACDP,'ND')                                                                           " Fecha De Rp", 
    RES.TIPO||'-'||RES.COMPROBANTES                                                                  " Número De Rp",                                                                     
    OCR.RUBRONOMBRE                                                                                  " Rubro Del Rp",
    NVL(RES.TOTAL_RES,0)                                                                             " Valor De Rp",
    'ND'                                                                                             "Fecha Aprobación Garantía Unica",
    TO_CHAR(OC.FECHAINICIO,'DD/MM/YYYY')                                                             "Fecha Iniciación",
    OC.PLAZODEENTREGA                                                                                "Plazo Contrato",
    TO_CHAR(ADICIONES.FECHA,'DD/MM/YYYY')                                                            "Fecha Adición",
    ADICIONES.DURACION                                                                               "Plazo Adición",
    ADICIONES.ADICION                                                                                "Valor Adición",
    PAGO_CONTRATO.VALOR_PAGOS                                                                        "Valor Pagos Efectuados",
    TO_CHAR(OC.FECHAFINALIZA]') || TO_CLOB(q'[CION,'DD/MM/YYYY')                                                       "Fecha De Terminación",
    ''                                                                                               "Fecha De Acta De Liquidación"
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
    LEFT JOIN (SELECT ORDC.COMPANIA,
                ORDC.CLASE,
                ORDC.NUMEROAFECTADO,
                SUM(ORDC.VALORTOTAL) ADICION,
                ORDC.DURACION,
                ORDC.FECHA
            FROM ORDENDECOMPRA ORDC
            WHERE ORDC.COMPANIA = s$compania$s
            AND  TO_CHAR(ORDC.FECHA,'MM')<= s$mesFinal$s
            GROUP BY ORDC.CLASE,
                    ORDC.NUMEROAFECTADO,
                    ORDC.COMPANIA,
                    ORDC.DURACION,
                    ORDC.FECHA) ADICIONES
        ON OC.COMPANIA=ADICIONES.COMPANIA
        AND OC.NUMERO = ADICIONES.NUMEROAFECTADO
    LEFT JOIN (SELECT CN.COMPANIA,
                    CN.TIPOCONTRATO,
                    CN.NUMEROCONTRATO,
                    SUM(CN.VLR_DOCUMENTO)VALOR_PAGOS 
                    FROM COMPROBANTE_PPTAL CN
                    INNER JOIN TIPO_COMPROBPP TC
                    ON TC.COMPANIA = CN.COMPANIA
                    AND TC.CODIGO = CN.TIPO
                    WHERE CN.COMPANIA=s$compania$s
                    AND  CN.ANO= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(CN.FECHA,'MM'))<=s$mesFinal$s  
                    AND  TC.CLASE = 'EGR'
                    GROUP BY  CN.COMPANIA,
                        CN.TIPOCONTRATO,
                        CN.NUMEROCONTRATO) PAGO_CONTRATO
    ON OC.COMPANIA=PAGO_CONTRATO.COMPANIA
        AND  OC.CLASEORDEN=PAGO_CONTRATO.TIPOCONTRATO
        AND  OC.NUMERO=PAGO_CONTRATO.NUMEROCONTRATO      

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
    LEFT JOIN]') || TO_CLOB(q'[ POLIZAS POL
            ON  OC.COMPANIA=POL.COMPANIA
            AND OC.CLASEORDEN=POL.CLASEORDEN
            AND OC.NUMERO=POL.ORDENDECOMPRA
    LEFT JOIN ASEGURADORA
                ON ASEGURADORA.COMPANIA=POL.COMPANIA
                AND ASEGURADORA.NITASEGURADORA=POL.ASEGURADORA
                AND ASEGURADORA.SUCURSAL=POL.SUCURSAL 
    LEFT JOIN ORDENDECOMPRAPPTO
                ON ORDENDECOMPRAPPTO.COMPANIA=OC.COMPANIA
                AND ORDENDECOMPRAPPTO.TIPOPPTO= 'DIS'
                AND ORDENDECOMPRAPPTO.NUMERO=OC.NUMERO
                AND ORDENDECOMPRAPPTO.CLASEORDEN=OC.CLASEORDEN
    LEFT JOIN (SELECT DETCOMPTAL.FUENTE_RECURSO,
            DETCOMPTAL.TIPO_CPTE,
            DETCOMPTAL.COMPANIA,
            DETCOMPTAL.COMPROBANTE,
            DETCOMPTAL.ANO
    FROM DETALLE_COMPROBANTE_PPTAL DETCOMPTAL
    WHERE DETCOMPTAL.COMPANIA=s$compania$s
    AND  DETCOMPTAL.ANO= s$ano$s     
    GROUP BY DETCOMPTAL.FUENTE_RECURSO,
        DETCOMPTAL.TIPO_CPTE,
        DETCOMPTAL.COMPANIA,
        DETCOMPTAL.COMPROBANTE,
        DETCOMPTAL.ANO
        )GDETCOMPTAL   
    ON GDETCOMPTAL.COMPANIA = ORDENDECOMPRAPPTO.COMPANIA
    AND GDETCOMPTAL.COMPROBANTE = ORDENDECOMPRAPPTO.numeroppto
    AND GDETCOMPTAL.TIPO_CPTE = 'DIS'
    LEFT JOIN FUENTE_RECURSOS FREC    
    ON FREC.COMPANIA = GDETCOMPTAL.COMPANIA
    AND FREC.ANO = GDETCOMPTAL.ANO
    AND FREC.CODIGO = GDETCOMPTAL.FUENTE_RECURSO
WHERE OC.COMPANIA=s$compania$s
    AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
    AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s AND s$mesFinal$s
    AND  TC.CLASE = 'C'  
GROUP BY OC.NUMERO,
    FREC.NOMBRE,
    OC.OBJETOCONTRATO,
    TC.CODCONTTOLIMA,
    OC.VALORTOTAL,
    OC.NOMBRECONTRATISTA,
    OC.CEDULACONTRATISTA,
    DIS.TIPO||'-'||DIS.COMPROBANTES,
    DIS.FECHACDP, 
    NVL(DIS.TOTAL_DIS,0),
    CASE WHEN OC.FECHASUSCRIPCION IS NULL
    THEN TO_CHAR(OC.FECHA,'DD/MM/YYYY')
    ELSE TO_CHAR(OC.FECHAFIRMA,'DD/MM/YYYY') END,
    TA.EQUIVALENTESIA,
    NVL(RES.FECHACDP,'ND'), 
    RES.TIPO||'-'||RES.COMPROBANTES,                                                                     
    OCR.RUBRONOMBRE,
    NVL(RES.TOTAL_RES,0),
    'ND',
    TO_CHAR(OC.FECHAINICIO,'DD/MM/YYYY'),
    OC.PLAZODEENTREGA,
    ADICIONES.FECHA,
    ADICIONES.DURACION,
    ADICIONES.ADICION,
    PAGO_CONTRATO.VALOR_PAGOS,
    TO_CHAR(OC.FECHAFINALIZACION,'DD/MM/YYYY')
ORDER BY OC.NUMERO]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, 'calarcon' MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);