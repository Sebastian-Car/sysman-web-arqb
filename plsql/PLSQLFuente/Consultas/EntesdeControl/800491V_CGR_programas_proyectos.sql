MERGE INTO CONSULTAS FIN USING (SELECT '800491V_CGR_programas_proyectos' INFORME ,TO_CLOB(q'[SELECT DISTINCT TO_CHAR(PROYECTOS.VIGENCIAINICIO)                                               Anio,
                SUBSTR(COMPANIA.NITCOMPANIA ,1, INSTR(COMPANIA.NITCOMPANIA, '-', 1)-1)          NIT_Entidad, 
                COMPANIA.NOMBRE                                                                 Nombre_Entidad,
                NVL(PROYECTOS.CODIGOBPIM,  'NA')                                                Codigo_Banco_Proyecto,
                NVL(PROYECTOS.NOMBREPROYECTO, 'NA')                                             Nombre_Proyecto,
                REPLACE(NVL(PROYECTOS.VALORTOTAL, 0), '.', ',')                                 Valor_Proyecto,
                (NVL(PROYECTOS.VIGENCIAFIN, 0)-NVL(VIGENCIAINICIO, 0)+1)*365                    Duracion_Proyecto,
                NVL(DEPENDENCIA.NOMBRE , 'NA')                                                  Dependencia_Responsable_Proyecto,
                NVL(PROYECTOS.PRODUCTO , 'NA')                                                  Descripcion,
                NVL(PROYECTOS.OBJETO , 'NA')                                                    Objetivo_General_Proyecto,
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
                        )                                                                       Programa_Plan_Desarrollo_Municipal,
                TO_CHAR(NVL(PROYECTOS.VIGENCIAINICIO, 1900))||'-01-01'                          Fecha_Inicio_Ejecucion_Proyecto,
                TO_CHAR(NVL(PROYECTOS.VIGENCIAFIN, 1900))||'-12-31'                             Fecha_Cierre_Ejecucion_Proyecto,
                'NA'                                                                           Observaciones ,
                'NO'                                                                            Proviene_de_recurso_de_reactivacion
FROM PROYECTOS 
    INNER JOIN  COMPANIA
        ON PROYECTOS.COMPANIA=COMPANIA.CODIGO
    LEFT JOIN SECTORDNP
        ON  PROYECTOS.COMPANIA=SECTORDNP.COMPANIA
        AND PROYECTOS.SECTORDNP=SECTORDNP.CODIGODNP
    LEFT JOIN DEPENDENCIA
        ON  DEPENDEN]') || TO_CLOB(q'[CIA.COMPANIA=PROYECTOS.COMPANIA
        AND DEPENDENCIA.CODIGO=PROYECTOS.DEPENDENCIA 
WHERE PROYECTOS.COMPANIA=s$compania$s
AND PROYECTOS.VIGENCIAINICIO=s$ano$s]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);