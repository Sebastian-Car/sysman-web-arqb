SELECT  TO_CHAR(IP_RECIBOS_DE_PAGO.PREFECPAG,'YYYY') AS PREANO, 
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) IN (s$codigosRurales$s)
              THEN C1 + C13 s$conceptos$s
              ELSE 0
            END ) AS RURALACT, 
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) NOT IN(s$codigosRurales$s)
              THEN C1 + C13 s$conceptos$s
              ELSE 0
            END) AS URBANOACT, 
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) IN (s$codigosRurales$s)
              THEN C5
              ELSE 0
            END) AS RURALANT, 
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) NOT IN (s$codigosRurales$s)
              THEN C5
              ELSE 0
            END) AS URBANOANT,
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) IN (s$codigosRurales$s)
              THEN C9
              ELSE 0
              END) AS RURALANTS, 
        SUM(CASE WHEN SUBSTR(IP_RECIBOS_DE_PAGO.PRECOD,1,2) NOT IN(s$codigosRurales$s)
              THEN C9
              ELSE 0
            END) AS URBANOANTS 
FROM IP_RECIBOS_DE_PAGO 
WHERE IP_RECIBOS_DE_PAGO.COMPANIA = s$compania$s
  AND TO_CHAR(IP_RECIBOS_DE_PAGO.PREFECPAG,'YYYY') BETWEEN s$anoInicial$s AND s$anoFinal$s
  AND IP_RECIBOS_DE_PAGO.PAGO NOT IN(0)
  AND IP_RECIBOS_DE_PAGO.ANULADO IN(0)
GROUP BY TO_CHAR(IP_RECIBOS_DE_PAGO.PREFECPAG,'YYYY')
ORDER BY TO_CHAR(IP_RECIBOS_DE_PAGO.PREFECPAG,'YYYY')
