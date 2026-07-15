SELECT 
  SP_ORDENTRABAJO.COMPANIA,
  SP_ORDENTRABAJO.CLASEDOC,
  SP_ORDENTRABAJO.NUMORDEN,
  CASE SP_ORDENTRABAJO.TIPOREQUERIMIENTO  WHEN 'C' THEN 'Consulta'
                                          WHEN 'Q' THEN 'Queja'
                                          WHEN 'RE' THEN 'Recurso de Reposición'
                                          WHEN 'P' THEN 'Peticion'
                                          WHEN 'REA' THEN 'Recurso de Reposición y Subsidiario de Apelación'
  END TIPOREQUERIMIENTO,
  SP_ORDENTRABAJO.CICLO,
  SP_ORDENTRABAJO.CODIGORUTA,
  SP_USUARIO.PRIMERAPELLIDO||' '||SP_USUARIO.SEGUNDOAPELLIDO ||' ' ||SP_USUARIO.NOMBRES AS NOMBRES,
  CASE SP_ORDENTRABAJO.CAUSALREFAC  WHEN 1 THEN 'Si es reclamo directo del usuario' 
                                    WHEN 2 THEN 'Si es solución de segunda instancia de la SSPD'
                                    WHEN 3 THEN 'Si es resultado de una decisión de la empresa'
                                    WHEN 4 THEN 'Si no cumple alguna de las anteriores'
                                    WHEN 0 THEN 'No se presentó refacturación'
  END CAUSALREFAC,
  SP_ORDENTRABAJO.NUM_REFAC
FROM SP_ORDENTRABAJO 
  LEFT JOIN SP_USUARIO
     ON SP_ORDENTRABAJO.CODIGORUTA     = SP_USUARIO.CODIGORUTA
    AND SP_ORDENTRABAJO.CICLO         = SP_USUARIO.CICLO
    AND SP_ORDENTRABAJO.COMPANIA      = SP_USUARIO.COMPANIA
WHERE SP_ORDENTRABAJO.COMPANIA = '001'
  AND SP_ORDENTRABAJO.CLASEDOC    ='PQR'
  AND SP_ORDENTRABAJO.CICLO       = s$ciclo$s
  AND SP_ORDENTRABAJO.CAUSALREFAC NOT IN (0)
  AND SP_ORDENTRABAJO.NUM_REFAC  IN (0)
