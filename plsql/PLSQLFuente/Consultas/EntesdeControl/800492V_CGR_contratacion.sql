MERGE INTO CONSULTAS FIN USING (SELECT '800492V_CGR_contratacion' INFORME ,TO_CLOB(q'[SELECT TO_CHAR(OC.FECHA,'YYYY')                                                                          Anio,
       SUBSTR(C.NITCOMPANIA ,1, INSTR(C.NITCOMPANIA, '-', 1)-1)                                          NIT_Entidad,
       C.NOMBRE                                                                                          Nombre_Entidad,
       OC.CLASEORDEN||'-' ||OC.NUMERO                                                                    Numero_Del_Contrato, 
       'NA'                                                                                              Codigo_Banco_Proyecto,
       'NA'                                                                                              Linea_O_Estrategia_Desarrollada,
       NVL(OCR.FUENTERECURSO, 'NA')                                                                      Fuente_De_Recurso,
       REPLACE(REPLACE(REPLACE(OC.OBJETOCONTRATO, CHR(13), ' ') , CHR(10), ' ') , CHR(124), ' ')         Objeto, 
       NVL(CLASETIPOORDENDECOMPRA.DESCRIPCION , 'NA')                                                    Clase_De_Contrato,
       REPLACE(NVL(OC.VALORTOTAL, 0), '.', ',')                                                          Valor_Del_Contrato,
       NVL(OC.NOMBRECONTRATISTA, 'NA')                                                                   Nombre_Del_Contratista ,
       NVL(OC.CEDULACONTRATISTA, 0)                                                                      Identificacion_Del_Contratista,
       NVL(TERCERO.DIRECCION, 'NA')                                                                      Direccion_Domicilio_Principal,
       NVL(TERCERO.TELEFONOS, 'NA')                                                                      Telefono_De_Contacto,
       NVL(TERCERO.DIRECCIONEMAIL, 'NA')                                                                 Correo_Electronico,
       NVL(BANCO.NOMBREBANCO, 'NA')                                                                      Entidad_Bancaria,  
       CASE WHEN TERCEROPAGOS.TIPOCUENTA='A'
        THEN 'Ahorros' 
        ELSE CASE WHEN  TERCEROPAGOS.TIPOCUENTA='C' THEN  'Corriente'
        ELSE 'NA' END END                                                                               Tipo_Cuenta_Bancaria,
       NVL(TERCEROPAGOS.CUENTA, 'NA')                                                                   No_Cuenta_Bancaria,
       CASE WHEN INSTR(NVL(DIS.COMPROBANTES,0), ',')>1
         THEN SUBSTR(DIS.COMPROBANTES ,1, INSTR(DIS.COMPROBANTES, ',', 1)-1)
         ELSE NVL(DIS.COMPROBANTES,0) END                                                                      No_Disponibilidad_Presupuestal,
       SUBSTR(DIS.FECHACDP,7,4)||'-'||SUBSTR(DIS.FECHACDP,4,2)||'-'||SUBSTR(DIS.FECHACDP,1,2)           Fecha_Disponibilidad,       
       REPLACE(NVL(DIS.TOTAL_DIS, 0), '.', ',')                                                         Valor_Disponibilidad,      
        CASE WHEN OC.FECHAFIRMA IS ]') || TO_CLOB(q'[NULL
       THEN TO_CHAR(NVL(OC.FECHASUSCRIPCION, '01/01/1900'),'YYYY-MM-DD')
       ELSE TO_CHAR(NVL(OC.FECHAFIRMA, '01/01/1900'),'YYYY-MM-DD') END                                  Fecha_Firma_Contrato, 
       NVL(TA.DESCRIPCION, 'NA')                                                                        Forma_De_Contratacion,
       CASE WHEN RES.FECHACDP IS NULL 
        THEN '1900-01-01' ELSE 
            CASE WHEN INSTR(RES.FECHACDP, ',')>1
                THEN SUBSTR(RES.FECHACDP ,1, INSTR(RES.FECHACDP, ',', 1)-1)
                ELSE RES.FECHACDP END  END                                              Fecha_Registro_Presupuestal,
       Replace(NVL(RES.TOTAL_RES, 0), '.',',')                                                          Valor_Registro_Presupuestal,
--     OCR.RUBRONOMBRE                                                                                  Rubro Registro Presupuestal,
       NVL(TRIM(OCR.RUBRONOM_CCEPT), 'NA')                                                                    Cod_Rubro_Registro_Presupuestal,
       NVL(CUIPO_FUENTES.NOMBRE, 'NA')                                         Fuente_Financiacion_Registro_Presupuestal,
       'S'                                                                                              Asignado_Supervisor_O_Interventor,
       CASE WHEN OC.CEDULAINTERVENTOR ='999999999999999999'
          THEN  NVL(SUP.CEDULA, '0')
          ELSE NVL(OC.CEDULAINTERVENTOR, '0') END                                                      Identificacion_Interventor_O_Supervisor,
       CASE WHEN OC.INTERVENTOR  IS NULL 
          THEN  NVL(TER.NOMBRE, 'NA')
          ELSE NVL(OC.INTERVENTOR, 'NA') END                                                            Nombre_Completo_Del_Interventor ,
      CASE WHEN TIPOINTERVENTOR='E'
       THEN 'EXTERNO'
       ELSE CASE WHEN (TIPOINTERVENTOR='I' OR SUP.CEDULA IS NOT NULL)
           THEN 'INTERNO' ELSE 'NA' END  END                                                            Tipo_Vinculacion_Interventor_O_Supervisor,
      TO_CHAR(NVL(OC.FECHAPOLIZAS, '01/01/1900') , 'YYYY-MM-DD')                                        Fecha_Aprobacion_Garantia_Unica , 
      CASE WHEN OC.FECHAINICIO IS NULL
       THEN TO_CHAR(NVL(OC.FECHA, '01/01/1900'),'YYYY-MM-DD')
       ELSE TO_CHAR(NVL(OC.FECHAINICIO, '01/01/1900'),'YYYY-MM-DD') END                                 Fecha_Iniciacion_Contrato,
      CASE WHEN OC.PLAZODEENTREGA  IS NULL
        THEN  NVL(TRIM(OC.DURACION), 0) 
        ELSE  NVL(TRIM(OC.PLAZODEENTREGA), 0) END                                                       Plazo_Contrato,
       'DIAS'                                                                                           Unidad_Ejecucion, 
      CASE WHEN OC.ABONOS=0
        THEN 'N'
        ELSE 'S' END                                                                                    Anticipo_Al_Contrato,
      REPLACE(NVL(OC.ABONOS, 0), '.', ',')     ]') || TO_CLOB(q'[                                                         Valor_Pagado_Anticipo,
      '1900-01-01'                                                                                      Fecha_Pago_Anticipo,
      NVL(ADICIONES.NUMADICIONES ,0)                                                                    Numero_Adiciones,
      REPLACE(NVL(ADICIONES.ADICION,0), '.',',')                                                        Valor_Total_Adiciones,
      NVL(PRORROGA.NUMPRORROGA,0)                                                                       Numero_Prorrogas,
      NVL(PRORROGA.TIEMPOPRO,0 )                                                                        Tiempo_Prorrogas,
      NVL(SUSPENSION.NUMSUSP,0 )                                                                        Numero_Suspensiones,
      NVL(SUSPENSION.TIEMPOSUSP,0 )                                                                     Tiempo_Suspensiones,
      REPLACE(NVL(PAGO_CONTRATO.VALOR_PAGOS ,0), '.', ',')                                              Valor_Total_Pagos,
      TO_CHAR(NVL(OC.FECHAFINALIZACION, '01/01/1900') ,'YYYY-MM-DD')                                    Fecha_Terminacion_Contrato,
      TO_CHAR(NVL(OC.FECHALIQUIDACION, '01/01/1900') ,'YYYY-MM-DD')                                     Fecha_Acta_Liquidacion,
      CASE WHEN OC.ESTADO='L'
       THEN 'LIQUIDADO'
       ELSE CASE WHEN OC.ESTADO='T'
             THEN 'TERMINADO'
             ELSE CASE WHEN OC.ESTADO='M'
                    THEN 'TERMINADO MUTUO ACUERDO'
                    ELSE  CASE WHEN OC.ESTADO='S'
                           THEN 'SUSPENDIDO'
                           ELSE  CASE WHEN OC.ESTADO='A'
                                   THEN 'ANULADO'
                                   ELSE  CASE WHEN OC.ESTADO='V'
                                   THEN 'VIGENTE'
                                   ELSE 'NA' END END END END END  END                                    Estado_Contrato,
      'NA'                                                                                               Observaciones,
      'NO'                                                                                               Proviene_de_recurso_de_reactivacion 
FROM ORDENDECOMPRA OC 
        INNER JOIN COMPANIA C
            ON OC.COMPANIA=C.CODIGO
        INNER JOIN TERCERO 
            ON OC.COMPANIA=TERCERO.COMPANIA
            AND OC.TERCERO=TERCERO.NIT
            AND OC.SUCURSAL=TERCERO.SUCURSAL
        LEFT JOIN TERCEROPAGOS 
            ON TERCERO.COMPANIA=TERCEROPAGOS.COMPANIA
            AND TERCERO.NIT=TERCEROPAGOS.NIT
            AND TERCERO.SUCURSAL=TERCEROPAGOS.SUCURSAL
        LEFT JOIN BANCO
            ON TERCEROPAGOS.COMPANIA=BANCO.COMPANIA
            AND TERCEROPAGOS.BANCO=BANCO.BANCO
        LEFT JOIN TIPOADJUDICACION TA
            ON OC.COMPANIA=TA.COMPANIA
            AND OC.TIPOADJUDICACION=TA.CODIGO
        INNER JOIN TIPOORDENDECOMPR]') || TO_CLOB(q'[A TC
            ON OC.COMPANIA=TC.COMPANIA
            AND OC.CLASEORDEN=TC.CODIGO
        LEFT JOIN CLASETIPOORDENDECOMPRA
            ON TC.TIPOSIA = CLASETIPOORDENDECOMPRA.CODIGO
        LEFT JOIN SECTORES ST
            ON OC.COMPANIA=ST.COMPANIA
            AND OC.SECTOR=ST.CODIGO
        LEFT JOIN DEPENDENCIA DE
            ON OC.COMPANIA=DE.COMPANIA
            AND OC.DEPENDENCIA=DE.CODIGO
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
                    LEFT JOIN CLASENOVEDAD
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
        LEFT JOIN ( SELECT  N.COMPANIA,
                            N.CLASEORDEN,
                            N.ORDENDECOMPRA,
                            SUM(N.DIAS_CONTRATO) TIEMPOPRO ,
                            COUNT(N.VALORTOTAL) NUMPRORROGA
                    FROM NOVEDADCONTRATO N INNER JOIN CLASETRANSACCIONC
                        ON N.TIPOT=CLASETRANSACCIONC.TIPOT
                    WHERE N.COMPANIA=s$compania$s
      ]') || TO_CLOB(q'[              AND  N.ANO= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(N.FECHA,'MM'))<= s$mesFinal$s 
                    AND CLASETRANSACCIONC.CLASENOVEDAD IN('O','D','Z','M')
                    AND DIAS_CONTRATO NOT IN(0)
                    GROUP BY N.CLASEORDEN,
                        N.ORDENDECOMPRA,
                        N.COMPANIA
                    UNION ALL 
                    SELECT  ORDENDECOMPRA.COMPANIA,TIPOAFECTADO CLASEORDENORDEN, NUMEROAFECTADO ORDENDECOMPRA,
                            SUM(NVL(ORDENDECOMPRA.DURACION,0)) TIEMPOPRO,COUNT(ORDENDECOMPRA.DURACION) NUMPRORROGA
                    FROM ORDENDECOMPRA 
                        INNER JOIN TIPOORDENDECOMPRA
                            ON ORDENDECOMPRA.COMPANIA=TIPOORDENDECOMPRA.COMPANIA
                            AND ORDENDECOMPRA.CLASEORDEN=TIPOORDENDECOMPRA.CODIGO
                        LEFT JOIN CLASENOVEDAD
                            ON ORDENDECOMPRA.CLASENOVEDAD=CLASENOVEDAD.CODIGO
                    WHERE ORDENDECOMPRA.COMPANIA=s$compania$s
                    AND  TO_NUMBER( TO_CHAR(ORDENDECOMPRA.FECHA,'YYYY'))= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(ORDENDECOMPRA.FECHA,'MM'))<= s$mesFinal$s 
                    AND TIPOORDENDECOMPRA.CLASE='M'
                    AND ORDENDECOMPRA.CLASENOVEDAD IN('O','D','Z','M')
                    AND TIPOAFECTADO IS NOT NULL
                    AND NUMEROAFECTADO NOT IN(0)
                    GROUP BY ORDENDECOMPRA.COMPANIA,TIPOAFECTADO, NUMEROAFECTADO   ) PRORROGA              
            ON OC.COMPANIA=PRORROGA.COMPANIA
            AND  OC.CLASEORDEN=PRORROGA.CLASEORDEN
            AND  OC.NUMERO=PRORROGA.ORDENDECOMPRA                        
        LEFT JOIN ( SELECT N.COMPANIA,
                        N.CLASEORDEN,
                        N.ORDENDECOMPRA,
                        SUM(N.DIAS_CONTRATO) TIEMPOSUSP ,
                        COUNT(N.VALORTOTAL) NUMSUSP
                    FROM NOVEDADCONTRATO N 
                        INNER JOIN CLASETRANSACCIONC
                            ON N.TIPOT=CLASETRANSACCIONC.TIPOT
                    WHERE N.COMPANIA=s$compania$s
                    AND  N.ANO= s$ano$s
                    AND  TO_NUMBER(TO_CHAR(N.FECHA,'MM'))<= s$mesFinal$s 
                    AND CLASETRANSACCIONC.CLASENOVEDAD IN('A')
                    AND N.TIPOT IN('ACS')
                    GROUP BY N.CLASEORDEN,
                        N.ORDENDECOMPRA,
                        N.COMPANIA) SUSPENSION
            ON OC.COMPANIA=SUSPENSION.COMPANIA
            AND  OC.CLASEORDEN=SUSPENSION.CLASEORDEN
            AND  OC.NUMERO=SUSPENSION.ORDENDECOMPRA                   
        LEFT JOIN ( SELECT CN.COMPANIA,
                            CN.TIPOCONTRATO,
                            CN.NUMEROCONTRATO,
                            SUM(CN.VLR_DOCUMENTO)VALOR_PAGOS 
                    FROM COMPROBANTE_PPTAL CN
                    INNER JOIN TIPO_CO]') || TO_CLOB(q'[MPROBPP TC
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
        LEFT JOIN V_ORDENDECOMPRA_DIS DIS
            ON OC.COMPANIA=DIS.COMPANIA
            AND OC.CLASEORDEN=DIS.CLASEORDEN
            AND OC.NUMERO=DIS.NUMERO
        LEFT JOIN V_ORDENDECOMPRA_RES RES
            ON OC.COMPANIA=RES.COMPANIA
            AND OC.CLASEORDEN=RES.CLASEORDEN
            AND OC.NUMERO=RES.NUMERO       
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
        LEFT JOIN ORDENDECOMPRA_AUXILIAR
            ON OC.COMPANIA=ORDENDECOMPRA_AUXILIAR.COMPANIA
            AND OC.CLASEORDEN=ORDENDECOMPRA_AUXILIAR.CLASEORDEN
            AND OC.NUMERO=ORDENDECOMPRA_AUXILIAR.ORDENDECOMPRA 
        LEFT JOIN CUIPO_FUENTES
            ON  OCR.COMPANIA=CUIPO_FUENTES.COMPANIA
            AND s$ano$s=CUIPO_FUENTES.ANO
            AND OCR.RUBRO=CUIPO_FUENTES.CODIGO          

    WHERE   OC.COMPANIA=s$compania$s
       AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
       AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s  AND s$mesFinal$s 
       AND TC.CLASE NOT IN('M')]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);