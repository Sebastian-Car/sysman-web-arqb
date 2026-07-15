MERGE INTO CONSULTAS FIN USING (SELECT '000984EspecialLisAuxPptalCuentasFZAGZ' INFORME ,TO_CLOB(q'[SELECT
    det.compania,
    det.ano,
    det.consecutivo,
    det.id,
    det.referencia,
    det.sector,
    det.codigo_cpc,
    det.programa,
    det.cod_prod_cuipo PRODUCTO,
    com.dependencia,
    CASE
        WHEN s$tipoCuenta$s = 1 THEN
            det.cuenta
        ELSE
            lpad(nvl(plan.nivel1, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel2, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel3, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel4, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel5, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel6, ''), 3)
            || ' -'
            || lpad(nvl(plan.nivel7, ''), 3)
    END rubro,
    precede.predecesor   id_prede,
    plan.nombre          nombreplan,
    det.fuente_recurso,
    precede.nombre       nombre_pred,
    det.fecha,
    det.tipo_cpte,
    com.numero,
    ter_det.nombre       nombretercero,
    det.tercero,
    det.descripcion,
    CASE
        WHEN com.nro_documento IS NULL
             OR com.nro_documento = '0' THEN
            to_char(com.tipocontrato
                    || ' -'
                    || com.numerocontrato)
        ELSE
            com.nro_documento
    END nro_documento,
    det.valor_debito     valordebito,
    det.valor_credito    valorcredito,
    SUM(nvl(afec.sdebitos, 0)) debito_afectado,
    SUM(nvl(afec.screditos, 0)) credito_afectado,
    SUM(nvl(afec.mdebitos, 0)) modificacion_debito,
    SUM(nvl(afec.mcreditos, 0)) modificacion_credito,
    CASE
            WHEN clasecntpres.afectacion IN (
                'A',
                'R'
            )
                 OR ( clasecntpres.claseafectar IS NULL
                      AND clasecntpres.codigo NOT IN (
                'DIS'
            ) ) THEN
                0
            ELSE
                CASE
                    WHEN plan.naturaleza = 'D' THEN
                        det.valor_debito - det.valor_credito
                    ELSE
                        - det.valor_debito + det.valor_credito
                END
        END
    + SUM(
        CASE
            WHEN plan.naturaleza = 'D' THEN
                - nvl(afec.sdebitos, 0) + nvl(afec.screditos, 0) + nvl(afec.mdebitos, 0) - nvl(afec.mcreditos, 0)
            ELSE
                nvl(afec.sdebitos, 0) - nvl(afec.screditos, 0) - nvl(afec.mdebitos, 0) + nvl(afec.mcreditos, 0)
        END
    ) saldoporejecutaresp,
    CASE
        WHEN det.tipo_cpte_afect IS NULL THEN
            ' '
        ELSE
            det.tipo_cpte_afect
            || ' '
            || det.cmpte_afectado
    END documentoafectado,
    com.tipocontrato,
    com.numerocontrato
FROM
    comprobante_pptal                                                                                                                                                                                                         ]') || TO_CLOB(q'[                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       com
    INNER JOIN detalle_comprobante_pptal                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ]') || TO_CLOB(q'[                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    det ON com.compania = det.compania
                                                AND com.ano = det.ano
                                                AND com.tipo = det.tipo_cpte
                                                AND com.numero = det.comprobante
    INNER JOIN plan_presupuestal                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ]') || TO_CLOB(q'[                                                                                                                                                                                                                                                                                                                                                                                                                                       plan ON plan.compania = det.compania
                                         AND plan.ano = det.ano
                                         AND plan.codigo = det.cuenta
    INNER JOIN tercero                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ter_det ON det.compania = ter_det.compania
                                  AND det.sucursal = ter_det.sucursal
                                  AND det.tercero = ter_det.nit
    INNER JOIN tipo_comprobpp ON com.compania = tipo_comprobpp.compania
                      ]') || TO_CLOB(q'[           AND com.tipo = tipo_comprobpp.codigo
    INNER JOIN clasecntpres ON tipo_comprobpp.clase = clasecntpres.codigo
    LEFT JOIN (
        SELECT
            afectado.compania,
            afectado.ano_afect,
            afectado.tipo_cpte_afect,
            afectado.cmpte_afectado,
            afectado.consecutivoppto,
            SUM(
                CASE
                    WHEN NOT(clasecntpres.afectacion IN(
                        'A', 'R'
                    )) THEN
                        valor_debito + nvl(modif.mdebitos, 0)
                    ELSE
                        0
                END
            ) sdebitos,
            SUM(
                CASE
                    WHEN NOT(clasecntpres.afectacion IN(
                        'A', 'R'
                    )) THEN
                        valor_credito + nvl(modif.mcreditos, 0)
                    ELSE
                        0
                END
            ) screditos,
            SUM(
                CASE
                    WHEN(clasecntpres.afectacion IN(
                        'A', 'R'
                    )) THEN
                        valor_debito
                    ELSE
                        0
                END
            ) mdebitos,
            SUM(
                CASE
                    WHEN(clasecntpres.afectacion IN(
                        'A', 'R'
                    )) THEN
                        valor_credito
                    ELSE
                        0
                END
            ) mcreditos
        FROM
            detalle_comprobante_pptal                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                afectado
            INNER JOIN tipo_comprobpp ON afectado.compania = tipo_comprobpp.compania
                                         AND afectado.tipo_cpte = tipo_comprobpp.codigo
            INNER JOIN clasecntpres ON tipo_comprobpp.clase = clasecntpres.codigo
            LEFT JOIN (
                SELECT
                    modificacion.compania,
                    modificacion.ano_afect,
                    modificacion.tipo_cpte_afect,
                    modificacion.cmpte_afectado,
                    modificacion.consecutivoppto,
]') || TO_CLOB(q'[
                    SUM(valor_debito) mdebitos,
                    SUM(valor_credito) mcreditos
                FROM
                    detalle_comprobante_pptal modificacion
                    INNER JOIN tipo_comprobpp ON modificacion.compania = tipo_comprobpp.compania
                                                 AND modificacion.tipo_cpte = tipo_comprobpp.codigo
                    INNER JOIN clasecntpres ON tipo_comprobpp.clase = clasecntpres.codigo
                WHERE
                    modificacion.compania = s$compania$s
                    AND modificacion.ano = s$anio$s
                    AND modificacion.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
                    AND trunc(modificacion.fecha) BETWEEN TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s',
                    'DD/MM/YYYY')
                    AND clasecntpres.afectacion IN (
                        'A',
                        'R'
                    )
                GROUP BY
                    modificacion.compania,
                    modificacion.ano_afect,
                    modificacion.tipo_cpte_afect,
                    modificacion.cmpte_afectado,
                    modificacion.consecutivoppto
            ) modif ON modif.compania = afectado.compania
                       AND modif.ano_afect = afectado.ano
                       AND modif.tipo_cpte_afect = afectado.tipo_cpte
                       AND modif.cmpte_afectado = afectado.comprobante
                       AND modif.consecutivoppto = afectado.consecutivo
        WHERE
            afectado.compania = s$compania$s
            AND afectado.ano = s$anio$s
            AND trunc(afectado.fecha) BETWEEN TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s', 'DD/MM/YYYY'
            )
            AND afectado.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
        GROUP BY
            afectado.compania,
            afectado.ano_afect,
            afectado.tipo_cpte_afect,
            afectado.cmpte_afectado,
            afectado.consecutivoppto
    ) afec ON afec.compania = det.compania
              AND afec.ano_afect = det.ano
              AND afec.tipo_cpte_afect = det.tipo_cpte
              AND afec.cmpte_afectado = det.comprobante
              AND afec.consecutivoppto = det.consecutivo
    LEFT JOIN (
        SELECT
            planpre.compania,
            planpre.ano,
            planpre.codigo,
            MAX(pre.codigo) predecesor,
            planpre.nombre
        FROM
            plan_presupuestal   planpre
            LEFT JOIN plan_presupuestal   pre ON pre.compania = planpre.compania
                                               AND pre.ano = planpre.ano
                                               AND pre.codigo = substr(planpre.codigo, 1, length(pre.codigo))
                                               AND pre.codigo <> planpre.codigo
        WHERE
  ]') || TO_CLOB(q'[          planpre.compania = s$compania$s
            AND planpre.ano = s$anio$s
        GROUP BY
            planpre.compania,
            planpre.ano,
            planpre.codigo,
            planpre.nombre
    )                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      precede ON plan.compania = precede.compania
                 AND plan.ano = precede.ano
                 AND plan.codigo = precede.codigo
WHERE
    com.compania = s$compania$s
    AND com.ano = s$anio$s
    AND com.tipo BETWEEN 's$tipoInicial$s' AND 's$tipoFinal$s'
    AND trunc(com.fecha) BETWEEN TO_DATE('s$fechaInicial$s', 'DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s', 'DD/MM/YYYY')
    AND det.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
    AND det.tercero BETWEEN 's$terceroInicial$s' AND 's$terceroFinal$s'
    AND det.centro_costo BETWEEN 's$centroInicial$s' AND 's$centroFinal$s'
    AND det.fuente_recurso BETWEEN 's$fuenteInicial$s' AND 's$fuenteFinal$s'
    AND det.referencia BETWEEN 's$referenciaInicial$s' AND 's$referenciaFinal$s'
GROUP BY
    det.compania,
    det.ano,
    det.consecutivo,
    det.id,
    det.referencia,
    det.sector,
    det.codigo_cpc,
    det.programa,
    det.cod_prod_cuipo,
    com.dependencia,
    CASE
            WHEN s$tipoCuenta$s = 1 THEN
                det.cuenta
            ELSE
                lpad(nvl(plan.nivel1, ''), 3)
      ]') CONSULTA, 3 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);