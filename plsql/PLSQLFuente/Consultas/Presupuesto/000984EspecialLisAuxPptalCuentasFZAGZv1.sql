MERGE INTO CONSULTAS FIN USING (SELECT '000984EspecialLisAuxPptalCuentasFZAGZv1' INFORME ,TO_CLOB(q'[WITH
MODIF_TMP AS (
    SELECT
        modificacion.compania,
        modificacion.ano_afect,
        modificacion.tipo_cpte_afect,
        modificacion.cmpte_afectado,
        modificacion.consecutivoppto,
        SUM(valor_debito) AS mdebitos,
        SUM(valor_credito) AS mcreditos
    FROM detalle_comprobante_pptal modificacion
    INNER JOIN tipo_comprobpp t
        ON modificacion.compania = t.compania
       AND modificacion.tipo_cpte = t.codigo
    INNER JOIN clasecntpres c
        ON t.clase = c.codigo
    WHERE modificacion.compania = s$compania$s
      AND modificacion.ano = s$anio$s
      AND modificacion.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
      AND modificacion.fecha BETWEEN TO_DATE('s$fechaInicial$s','DD/MM/YYYY') 
                                AND TO_DATE('s$fechaFinal$s','DD/MM/YYYY')
      AND c.afectacion IN ('A','R')
    GROUP BY
        modificacion.compania,
        modificacion.ano_afect,
        modificacion.tipo_cpte_afect,
        modificacion.cmpte_afectado,
        modificacion.consecutivoppto
),
AFEC_TMP AS (
    SELECT
        afectado.compania,
        afectado.ano_afect,
        afectado.tipo_cpte_afect,
        afectado.cmpte_afectado,
        afectado.consecutivoppto,
        SUM(CASE WHEN NOT(c.afectacion IN ('A','R')) THEN afectado.valor_debito + NVL(modif.mdebitos,0) ELSE 0 END) AS sdebitos,
        SUM(CASE WHEN NOT(c.afectacion IN ('A','R')) THEN afectado.valor_credito + NVL(modif.mcreditos,0) ELSE 0 END) AS screditos,
        SUM(CASE WHEN c.afectacion IN ('A','R') THEN afectado.valor_debito ELSE 0 END) AS mdebitos,
        SUM(CASE WHEN c.afectacion IN ('A','R') THEN afectado.valor_credito ELSE 0 END) AS mcreditos
    FROM detalle_comprobante_pptal afectado
    INNER JOIN tipo_comprobpp t
        ON afectado.compania = t.compania
       AND afectado.tipo_cpte = t.codigo
    INNER JOIN clasecntpres c
        ON t.clase = c.codigo
    LEFT JOIN MODIF_TMP modif
        ON modif.compania = afectado.compania
       AND modif.ano_afect = afectado.ano
       AND modif.tipo_cpte_afect = afectado.tipo_cpte
       AND modif.cmpte_afectado = afectado.comprobante
       AND modif.consecutivoppto = afectado.consecutivo
    WHERE afectado.compania = s$compania$s
      AND afectado.ano = s$anio$s
      AND afectado.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
      AND afectado.fecha BETWEEN TO_DATE('s$fechaInicial$s','DD/MM/YYYY') 
                            AND TO_DATE('s$fechaFinal$s','DD/MM/YYYY')
    GROUP BY
        afectado.compania,
        afectado.ano_afect,
        afectado.tipo_cpte_afect,
        afectado.cmpte_afectado,
        afectado.consecutivoppto
),
PRECEDE_TMP AS (
    SELECT
        planpre.compania,
        planpre.ano,
        planpre.codigo,
        MAX(pre.codigo) AS predecesor,
        planpre.nombre
    FROM plan_presupuestal planpre
    LEFT JOIN plan_presupuestal pre
        ON pre.compania = planpre.compania
       AND pre.ano = planpre.ano
       AND pre]') || TO_CLOB(q'[.codigo = SUBSTR(planpre.codigo,1,LENGTH(pre.codigo))
       AND pre.codigo <> planpre.codigo
    WHERE planpre.compania = s$compania$s
      AND planpre.ano = s$anio$s
    GROUP BY
        planpre.compania,
        planpre.ano,
        planpre.codigo,
        planpre.nombre
)
SELECT
    det.compania,
    det.ano,
    det.consecutivo,
    det.id,
    det.referencia,
    det.sector,
    det.codigo_cpc,
    det.programa,
    det.cod_prod_cuipo AS PRODUCTO,
    com.dependencia,
    CASE
        WHEN s$tipoCuenta$s = 1 THEN det.cuenta
        ELSE lpad(nvl(plan.nivel1,''),3)||' -'||lpad(nvl(plan.nivel2,''),3)
             ||' -'||lpad(nvl(plan.nivel3,''),3)||' -'||lpad(nvl(plan.nivel4,''),3)
             ||' -'||lpad(nvl(plan.nivel5,''),3)||' -'||lpad(nvl(plan.nivel6,''),3)
             ||' -'||lpad(nvl(plan.nivel7,''),3)
    END AS rubro,
    precede.predecesor AS id_prede,
    plan.nombre AS nombreplan,
    det.fuente_recurso,
    precede.nombre AS nombre_pred,
    det.fecha,
    det.tipo_cpte,
    com.numero,
    ter_det.nombre AS nombretercero,
    det.tercero,
    com.descripcion,
    CASE
        WHEN com.nro_documento IS NULL OR com.nro_documento = '0' THEN
            TO_CHAR(com.tipocontrato||' -'||com.numerocontrato)
        ELSE com.nro_documento
    END AS nro_documento,
    det.valor_debito AS valordebito,
    det.valor_credito AS valorcredito,
    NVL(afec.sdebitos,0) AS debito_afectado,
    NVL(afec.screditos,0) AS credito_afectado,
    NVL(afec.mdebitos,0) AS modificacion_debito,
    NVL(afec.mcreditos,0) AS modificacion_credito,
    CASE
        WHEN clasecntpres.afectacion IN ('A','R') 
             OR (clasecntpres.claseafectar IS NULL AND clasecntpres.codigo NOT IN ('DIS')) THEN 0
        ELSE CASE WHEN plan.naturaleza='D' THEN det.valor_debito - det.valor_credito
                  ELSE -det.valor_debito + det.valor_credito END
    END
    + NVL(CASE WHEN plan.naturaleza='D' THEN -NVL(afec.sdebitos,0)+NVL(afec.screditos,0)+NVL(afec.mdebitos,0)-NVL(afec.mcreditos,0)
               ELSE NVL(afec.sdebitos,0)-NVL(afec.screditos,0)-NVL(afec.mdebitos,0)+NVL(afec.mcreditos,0) END,0)
    AS saldoporejecutaresp,
    CASE
        WHEN det.tipo_cpte_afect IS NULL THEN ' '
        ELSE det.tipo_cpte_afect||' '||det.cmpte_afectado
    END AS documentoafectado,
    com.tipocontrato,
    com.numerocontrato
FROM comprobante_pptal com
INNER JOIN detalle_comprobante_pptal det
    ON com.compania = det.compania
   AND com.ano = det.ano
   AND com.tipo = det.tipo_cpte
   AND com.numero = det.comprobante
INNER JOIN plan_presupuestal plan
    ON plan.compania = det.compania
   AND plan.ano = det.ano
   AND plan.codigo = det.cuenta
INNER JOIN tercero ter_det
    ON det.compania = ter_det.compania
   AND det.sucursal = ter_det.sucursal
   AND det.tercero = ter_det.nit
INNER JOIN tipo_comprobpp
    ON com.compania = tipo_comprobpp.compania
   AND com.tipo = tipo_comprobpp.codigo
INNER JOIN clasecntpres
    ON tipo_comprobpp.clase = clasecntpres.codigo
LEF]') || TO_CLOB(q'[T JOIN AFEC_TMP afec
    ON afec.compania = det.compania
   AND afec.ano_afect = det.ano
   AND afec.tipo_cpte_afect = det.tipo_cpte
   AND afec.cmpte_afectado = det.comprobante
   AND afec.consecutivoppto = det.consecutivo
LEFT JOIN PRECEDE_TMP precede
    ON plan.compania = precede.compania
   AND plan.ano = precede.ano
   AND plan.codigo = precede.codigo
WHERE com.compania = s$compania$s
  AND com.ano = s$anio$s
  AND com.tipo BETWEEN 's$tipoInicial$s' AND 's$tipoFinal$s'
  AND com.fecha BETWEEN TO_DATE('s$fechaInicial$s','DD/MM/YYYY') AND TO_DATE('s$fechaFinal$s','DD/MM/YYYY')
  AND det.cuenta BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
  AND det.tercero BETWEEN 's$terceroInicial$s' AND 's$terceroFinal$s'
  AND det.centro_costo BETWEEN 's$centroInicial$s' AND 's$centroFinal$s'
  AND det.fuente_recurso BETWEEN 's$fuenteInicial$s' AND 's$fuenteFinal$s'
  AND det.referencia BETWEEN 's$referenciaInicial$s' AND 's$referenciaFinal$s'
ORDER BY det.id]') CONSULTA, 3 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);
