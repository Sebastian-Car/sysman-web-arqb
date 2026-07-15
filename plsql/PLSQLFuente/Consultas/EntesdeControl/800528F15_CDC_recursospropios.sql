MERGE INTO CONSULTAS FIN USING (SELECT '800528F15_CDC_recursospropios' INFORME ,TO_CLOB(q'[SELECT OC.CLASEORDEN||'-' ||OC.NUMERO                                                                    No_De_Contrato, 
       NVL(RECURSOS.FTENOM, 'NA')                                                                       Fuente_De_Financiacion,
       REPLACE(REPLACE(REPLACE(OC.OBJETOCONTRATO, CHR(13), ' ') , CHR(10), ' ') , CHR(124), ' ')         Objeto_contractual, 
       NVL(OC.CEDULACONTRATISTA, 0)                                                                      Nit,
       NVL(OC.NOMBRECONTRATISTA, 'NA')                                                                   Contratista ,
       NVL(OC.VALORTOTAL, 0)                                                                             Valor,  
       NVL(ADICIONES.ADICION,0)                                                                          Valor_Adicion, 
       NVL(OC.VALORTOTAL, 0) +  NVL(ADICIONES.ADICION,0)                                                 Valor_Total,  
      CASE WHEN OC.FECHAFIRMA IS NULL
       THEN TO_CHAR(OC.FECHASUSCRIPCION,'YYYY-MM-DD')
       ELSE TO_CHAR(OC.FECHAFIRMA,'YYYY-MM-DD') END                                                       Fecha_Firma, 
     CASE WHEN OC.FECHAINICIO IS NULL
        THEN  TO_CHAR(OC.FECHA ,'YYYY-MM-DD') 
        ELSE 
       TO_CHAR(OC.FECHAINICIO ,'YYYY-MM-DD')END                                                           Fecha_de_Inicio,
       TO_CHAR(OC.FECHAFINALIZACION ,'YYYY-MM-DD')                                                        Fecha_de_Terminacion
                                         
      
FROM ORDENDECOMPRA OC 
    INNER JOIN TIPOORDENDECOMPRA TC
            ON OC.COMPANIA=TC.COMPANIA
            AND OC.CLASEORDEN=TC.CODIGO

        LEFT JOIN (SELECT N.COMPANIA,
                        N.CLASEORDEN,
                        N.ORDENDECOMPRA,
                        SUM(N.VALORTOTAL) ADICION ,
                        COUNT(NVL(N.VALORTOTAL,0))NUMADICIONES
                    FROM NOVEDADCONTRATO N INNER JOIN CLASETRANSACCIONC
                        ON N.TIPOT=CLASETRANSACCIONC.TIPOT
                    WHERE N.COMPANIA=s$compania$s
                    AND  N.ANO= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(N.FECHA,'MM'))<= s$mesFinal$s 
                    AND CLASETRANSACCIONC.CLASENOVEDAD IN('O','D','Z','M')  
                    AND VALORTOTAL NOT IN(0)
                    GROUP BY    N.CLASEORDEN,
                                N.ORDENDECOMPRA,
                                N.COMPANIA
                    UNION ALL 
                    SELECT  ORDENDECOMPRA.COMPANIA,TIPOAFECTADO CLASEORDENORDEN, NUMEROAFECTADO ORDENDECOMPRA,
                        SUM(ORDENDECOMPRA.VALORTOTAL) ADICION,COUNT(ORDENDECOMPRA.VALORTOTAL) NUMADICIONES
                    FROM ORDENDECOMPRA INNER JOIN TIPOORDENDECOMPRA
                      ON ORDENDECOMPRA.COMPANIA=TIPOORDENDECOMPRA.COMPANIA
                      AND ORDENDECOMPRA.CLASEORDEN=TIPOORDENDECOMPRA.CODIGO
                    LEFT JOIN CLASEN]') || TO_CLOB(q'[OVEDAD
                        ON ORDENDECOMPRA.CLASENOVEDAD=CLASENOVEDAD.CODIGO
                    WHERE ORDENDECOMPRA.COMPANIA=s$compania$s
                    AND  TO_NUMBER( TO_CHAR(ORDENDECOMPRA.FECHA,'YYYY'))= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(ORDENDECOMPRA.FECHA,'MM'))<= s$mesFinal$s 
                    AND TIPOORDENDECOMPRA.CLASE='M'
                    AND ORDENDECOMPRA.CLASENOVEDAD IN('O','D','Z','M')
                    AND TIPOAFECTADO IS NOT NULL
                    AND NUMEROAFECTADO NOT IN(0)
                    AND VALORTOTAL NOT IN(0)
                    GROUP BY ORDENDECOMPRA.COMPANIA,TIPOAFECTADO, NUMEROAFECTADO) ADICIONES
            ON OC.COMPANIA=ADICIONES.COMPANIA
            AND  OC.CLASEORDEN=ADICIONES.CLASEORDEN
            AND  OC.NUMERO=ADICIONES.ORDENDECOMPRA                          
          
        INNER JOIN V_ORDENDECOMPRA_FTERECURSO RECURSOS
            ON OC.COMPANIA=RECURSOS.COMPANIA
            AND OC.CLASEORDEN=RECURSOS.CLASEORDEN
            AND OC.NUMERO=RECURSOS.NUMERO  
      INNER  JOIN  FUENTE_RECURSOS
        ON  OC.COMPANIA=FUENTE_RECURSOS.COMPANIA
        AND TO_NUMBER(TO_CHAR(OC.FECHA,'YYYY'))=FUENTE_RECURSOS.ANO
        AND RECURSOS.FUENTE_RECURSO=FUENTE_RECURSOS.CODIGO
    WHERE   OC.COMPANIA=s$compania$s
       AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
       AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s  AND s$mesFinal$s 
       AND   TC.CLASE NOT IN('M')
       AND FUENTE_RECURSOS.TIPO IN('P','PD')]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);