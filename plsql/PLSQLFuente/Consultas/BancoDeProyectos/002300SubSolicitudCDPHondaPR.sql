MERGE INTO CONSULTAS FIN USING (SELECT '002300SubSolicitudCDPHondaPR' INFORME ,TO_CLOB(q'[WITH saldos AS (
    SELECT
        compania,
        ano,
        id,
        SUM(aprdefinitiva) apropiacion_inicial,
        SUM(disponibilidad) disponibilidad,
        SUM(reg_no_contract) AS totalejecutado,
        SUM(aprdefinitiva - reg_no_contract) AS saldoxejec,
        CASE
            WHEN SUM(aprdefinitiva) NOT IN (
                0
            ) THEN
                pck_sysman_utl.fc_round((SUM(reg_no_contract) * 100) / SUM(aprdefinitiva), 2)
            ELSE
                0
        END AS porcecjecutar
    FROM
        v_resumenppto_base
    WHERE
        naturaleza IN (
            'D'
        )
    GROUP BY
        compania,
        ano,
        id
)
SELECT
    v_solicitud_cdp.rubro                          rubropresupuestal,
    plan_presupuestal.nombre                       AS nombre_presupuestal,
    fuente_recursos.codigo
    || ' '
    || fuente_recursos.nombre                      AS fuente,
    referencia.codigo
    || ' '
    || referencia.nombre                           AS referencia,
    v_solicitud_cdp.valorsolicitado,
    v_solicitud_cdp.descripcion_metaproducto       descripcion_metaproduct,
    v_solicitud_cdp.descripcion_indicador_medida   descripcion_metaproducto,
    saldos.apropiacion_inicial                     AS apropiacioninic,
    saldos.totalejecutado,
    saldos.saldoxejec,
    saldos.porcecjecutar,
    fuente_recursos.nombre
,
    CENTRO_COSTO.codigo
    || ' '
    || CENTRO_COSTO.NOMBRE                      AS NOMBRE_CENTRO
FROM
    v_solicitud_cdp
    INNER JOIN plan_presupuestal ON v_solicitud_cdp.compania = plan_presupuestal.compania
                                    AND v_solicitud_cdp.anorubro = plan_presupuestal.ano
                                    AND v_solicitud_cdp.rubro = plan_presupuestal.codigo
                                    
    inner join  v_plan_presupuestal on v_solicitud_cdp.compania             = v_plan_presupuestal.compania
    and v_solicitud_cdp.anorubro            = v_plan_presupuestal.ano
    and v_solicitud_cdp.rubro               = v_plan_presupuestal.codigo   
    and v_solicitud_cdp.fuenterecursos      = v_plan_presupuestal.fuente_recurso
    and v_solicitud_cdp.auxiliar            = v_plan_presupuestal.auxiliar
    and v_solicitud_cdp.referencia          = v_plan_presupuestal.referencia      
    and v_solicitud_cdp.centro_costo        = v_plan_presupuestal.centro_costo
    INNER JOIN saldos ON v_plan_presupuestal.compania = saldos.compania
                         AND v_plan_presupuestal.ano = saldos.ano
                         AND v_plan_presupuestal.id = saldos.id
    INNER JOIN fuente_recursos ON v_solicitud_cdp.compania = fuente_recursos.compania
                                  AND v_solicitud_cdp.vigencia = fuente_recursos.ano
                                  AND v_solicitud_cdp.fuenterecursos = fuente_recursos.codigo
    INNER JOIN referencia ON v_solicitud_cdp.compania = referencia.compania
                        AND v_solicitud_cdp.VIGENCIA  = refere]') || TO_CLOB(q'[ncia.ano
                             AND v_solicitud_cdp.referencia = referencia.codigo
    INNER JOIN centro_costo ON v_solicitud_cdp.compania = centro_costo.compania
                             AND v_solicitud_cdp.VIGENCIA  = centro_costo.ano
                             AND v_solicitud_cdp.centro_costo = centro_costo.codigo                            
WHERE
     V_SOLICITUD_CDP.COMPANIA      = s$compania$s
     AND  V_SOLICITUD_CDP.TIPOT        = 's$tipot$s'
     AND V_SOLICITUD_CDP.CLASET        = 's$claset$s'
     AND V_SOLICITUD_CDP.CONSECUTIVO   = s$consecutivo$s
     AND V_SOLICITUD_CDP.DEPENDENCIA   = s$dependencia$s]') CONSULTA, 52 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, 'ACELEITA' CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);

MERGE INTO CONSULTAS_SUB FIN USING (SELECT '002450SolicitudCDPHonda' INFORME_PADRE, '002300SubSolicitudCDPHondaPR' INFORME_HIJO, 'PR_STRSQL_SUB_SOLICITUDSDP_HONDA_PR' PARAMETRO ,'JCROJAS' CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (FIN.INFORME_PADRE = INI.INFORME_PADRE AND INI.INFORME_HIJO = FIN.INFORME_HIJO)  WHEN MATCHED THEN  UPDATE SET FIN.PARAMETRO = INI.PARAMETRO , FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME_PADRE,INFORME_HIJO, PARAMETRO,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME_PADRE,INI.INFORME_HIJO, INI.PARAMETRO,INI.CREATED_BY,SYSDATE);