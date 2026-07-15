SELECT
	*
FROM
	(
        WITH INI AS(
	SELECT
		dcc.CUENTA ,
		pc.NOMBRE AS CUENTANOMBRE,
		dcc.CENTRO_COSTOI || ' - ' || cc.NOMBRE AS CENTRO_COSTO,
		dcc.VALOR_DEBITO - dcc.VALOR_CREDITO AS NETO
	FROM
		DETALLE_COMPROBANTE_CNT dcc
	INNER JOIN CENTRO_COSTO cc
            ON
		dcc.COMPANIA = cc.COMPANIA
		AND dcc.ANO = cc.ANO
		AND dcc.CENTRO_COSTOI = cc.CODIGO
	INNER JOIN PLAN_CONTABLE pc
            ON
		dcc.COMPANIA = pc.COMPANIA
		AND dcc.ANO = pc.ANO
		AND dcc.CUENTA = pc.CODIGO
	WHERE
		dcc.COMPANIA = s$compania$s
		AND dcc.ANO = s$anoTrabajo$s
		AND dcc.TIPO_CPTE BETWEEN 's$tipoInicial$s' AND 's$tipoFinal$s'
		AND dcc.CUENTA BETWEEN 's$codigoInicial$s' AND 's$codigoFinal$s'
		AND dcc.TERCEROI BETWEEN 's$terceroInicial$s' AND 's$terceroFinal$s'
		AND dcc.CENTRO_COSTOI BETWEEN 's$centroInicial$s' AND 's$centroFinal$s'
		AND dcc.FUENTE_RECURSOI BETWEEN 's$auxiliarInicial$s' AND 's$auxiliarFinal$s'
		AND dcc.MES BETWEEN 's$mesInicial$s' AND 's$mesFinal$s'          
         )
	SELECT
		CUENTA ,
		CUENTANOMBRE,
		CENTRO_COSTO,
		SUM(NETO) NETO
	FROM
		INI
	GROUP BY
		CUENTA ,
		CUENTANOMBRE,
		CENTRO_COSTO
UNION ALL
	SELECT
		CUENTA ,
		CUENTANOMBRE,
		'Total Cuenta' CENTRO_COSTO,
		SUM(NETO) NETO
	FROM
		INI
	GROUP BY
		CUENTA ,
		CUENTANOMBRE
) x
pivot
(
Sum(NETO)
FOR (CENTRO_COSTO) IN (s$pivot$s)
)
ORDER BY
	CUENTA