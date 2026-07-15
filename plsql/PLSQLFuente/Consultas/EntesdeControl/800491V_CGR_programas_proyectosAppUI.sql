MERGE INTO CONSULTAS FIN USING (SELECT '800491V_CGR_programas_proyectosAppUI' INFORME ,TO_CLOB(q'[SELECT DISTINCT NVL(PROYECTOS.CODIGOBPIM,  'NA')                                               idBpin,
                NVL(PROYECTOS.NOMBREPROYECTO, 'NA')                                            nombreProyecto,
                NVL(PROYECTOS.VALORTOTAL, 0)                                                   valorProyecto,
               (NVL(PROYECTOS.VIGENCIAFIN, 0)-NVL(VIGENCIAINICIO, 0)+1)*365                    duracionProyecto, 
                NVL(DEPENDENCIA.NOMBRE , 'NA')                                                  dependenciaProyecto,
                NVL(PROYECTOS.PRODUCTO , 'NA')                                                  descripcion,
                NVL(PROYECTOS.OBJETO , 'NA')                                                    objetivoGeneral,
                NVL(SECTORDNP. NOMBRE, 'NA')                                                    Sector,
               (SELECT  DISTINCT  NVL(BP_PLAN_INDICATIVO.DESCRIPCION , 'NA')
                        FROM BP_PROYECTO_PLAN_INDICATIVO                          
                            INNER JOIN BP_PLAN_INDICATIVO
                                ON BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = BP_PLAN_INDICATIVO.COMPANIA
                                AND SUBSTR(ID_PLAN_P, 1, PCK_SYSMAN_UTL.FC_PAR('001','NUMERO DE DIGITOS PROGRAMA', 52, SYSDATE)) = BP_PLAN_INDICATIVO.ID
                        WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = s$compania$s
                        AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO = PROYECTOS.CODIGO
                        AND BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P = PCK_SYSMAN_UTL.FC_PAR('001','VIGENCIA GUBERNAMENTAL ACTUAL', 52, SYSDATE)
                        AND ROWNUM = 1
                        )                                                                       programaPlanDesarrollo,
                TO_CHAR(NVL(PROYECTOS.VIGENCIAINICIO, 1900))||'-01-01'                          fechaInicioEjecucion,
                TO_CHAR(NVL(PROYECTOS.VIGENCIAFIN, 1900))||'-12-31'                             fechaCierreEjecucion,
                'NA'                                                                            observaciones 

FROM PROYECTOS 
    INNER JOIN  COMPANIA
        ON PROYECTOS.COMPANIA=COMPANIA.CODIGO
    LEFT JOIN SECTORDNP
        ON  PROYECTOS.COMPANIA=SECTORDNP.COMPANIA
        AND PROYECTOS.SECTORDNP=SECTORDNP.CODIGODNP
    LEFT JOIN DEPENDENCIA
        ON  DEPENDENCIA.COMPANIA=PROYECTOS.COMPANIA
        AND DEPENDENCIA.CODIGO=PROYECTOS.DEPENDENCIA 
WHERE PROYECTOS.COMPANIA=s$compania$s
AND PROYECTOS.VIGENCIAINICIO=s$ano$s]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);