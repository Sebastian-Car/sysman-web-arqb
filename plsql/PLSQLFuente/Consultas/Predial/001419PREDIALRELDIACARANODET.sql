SELECT 
  CASE
    WHEN RE.ESABONO <> 0
    THEN 'Abonos'
    ELSE
      CASE
        WHEN RE.ESACUERDO <> 0
        THEN 'Acuerdos de Pago'
        ELSE 'Corriente'
      END
  END AS TIPOF,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN RE.DOCNUM
    ELSE DR.DOCNUM
  END AS RECIBO,
  U.CODIGO,
  U.NOMBRE,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN RE.PREANO
    ELSE DR.PREANO
  END AS VIGENCIA,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN RE.C1 + RE.C5 + RE.C9 + NVL(s$descImp$s,0)
    ELSE DR.C1 + NVL(s$descImpD$s,0)
  END AS IMPUESTOPREDIAL,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN RE.C3 + RE.C7 + RE.C11 + NVL(s$descCar$s,0)
    ELSE DR.C3 + NVL(s$descCarD$s,0)
  END AS CAR,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN RE.C4 + RE.C8 +RE.C12
    ELSE DR.C4
  END AS INTERESCAR,
  CASE
    WHEN (
      CASE
        WHEN RE.ESABONO <> 0
        THEN 'Abonos'
        ELSE
          CASE
            WHEN RE.ESACUERDO <> 0
            THEN 'Acuerdos de Pago'
            ELSE 'Corriente'
          END
      END) <> 'Corriente'
    THEN NVL(RE.C3,0)+ NVL(RE.C4,0) + NVL(RE.C7,0) + NVL(RE.C8,0) + NVL(RE.C11,0)+NVL(RE.C12,0) + NVL(s$descCar$s,0)
    ELSE NVL(DR.C3,0)+NVL(DR.C4,0) + s$descCarD$s
  END AS VALORFACTURADO,
  RE.PAG_BANPAG,
  B.NOMBREBANCO,
  RE.PREFECPAG
FROM IP_RECIBOS_DE_PAGO RE  
LEFT JOIN IP_DETALLE_RECIBOPAGO DR 
ON RE.COMPANIA = DR.COMPANIA 
AND RE.DOCNUM  = DR.DOCNUM 
AND RE.PREANO  = DR.PREANO 
INNER JOIN IP_USUARIOS_PREDIAL U 
ON RE.COMPANIA      = U.COMPANIA 
AND RE.PRECOD       = U.CODIGO 
AND RE.NUMERO_ORDEN = U.NUMERO_ORDEN 
INNER JOIN IP_BANCOS B 
ON RE.COMPANIA    = B.COMPANIA 
AND RE.PAG_BANPAG = B.CODIGOBANCO 
WHERE RE.PAG_BANPAG BETWEEN 's$codigoBancoInicial$s' AND 's$codigoBancoFin$s' 
AND RE.PAG_BANPAG <> s$prcodbanccompen$s 
AND RE.PREFECPAG BETWEEN s$fechaInicial$s AND s$fechaFinal$s
AND RE.PAG_BANPAG NOT IN('s$codBanCompens$s')
AND RE.ANULADO     = 0 
AND RE.PAGO       <> 0 
AND U.NUMERO_ORDEN = '001' 
AND RE.PAQUETEPAG BETWEEN 's$cmbPaqIni$s' AND 's$cmbPaqFin$s' 
ORDER BY RECIBO
