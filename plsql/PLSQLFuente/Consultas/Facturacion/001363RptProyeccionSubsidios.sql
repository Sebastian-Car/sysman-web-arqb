SELECT   EC.SERVICIO
        ,PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA   => s$compania$s,
                                                 UN_ANO        =>s$anioInicial$s,
                                                 UN_PERIODO    =>s$periodoInicial$s,
                                                 UN_FRECUENCIA =>NULL) NOM_MESINICIAL
        ,PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA   => s$compania$s,
                                                 UN_ANO        =>s$anioFinal$s,
                                                 UN_PERIODO    =>s$periodoFinal$s,
                                                 UN_FRECUENCIA =>NULL) NOM_MESFINAL
        ,S.NOMBRE NOM_SERVICIO
        ,U.NOMBRE NOM_USO
        ,E.NOMBRE NOM_ESTRATO
        ,SUM(EC.SUBSIDIO) SUB
        ,SUM(EC.SOBREPRECIO) SOB
        ,s$porcentaje$s PORC
        ,SUM(EC.SUBSIDIO * (s$porcentaje$s + 1)) SUB_PRO
        ,SUM(EC.SOBREPRECIO * (s$porcentaje$s + 1)) SOB_PRO
FROM     SP_ESTADISTICAS_CONSUMO EC
  INNER JOIN SP_TARIFAS T
     ON      EC.COMPANIA = T.COMPANIA
    AND      EC.ANO      = T.ANO
    AND      EC.PERIODO  = T.PERIODO
    AND      EC.USO      = T.USO
    AND      EC.ESTRATO  = T.ESTRATO
  INNER JOIN SP_ESTRATOS E
     ON      T.COMPANIA = E.COMPANIA
    AND      T.USO      = E.USO
    AND      T.ESTRATO  = E.CODIGO
  INNER JOIN SP_USOS U
     ON      T.COMPANIA = U.COMPANIA
    AND      T.USO      = U.CODIGO
  INNER JOIN SP_SERVICIO S
     ON      EC.COMPANIA = S.COMPANIA
    AND      EC.SERVICIO = S.CODIGO
WHERE    EC.COMPANIA = s$compania$s
  AND    EC.ANO BETWEEN s$anioInicial$s AND s$anioFinal$s
s$ciclo$s
  AND    EC.PERIODO BETWEEN s$periodoInicial$s AND s$periodoFinal$s
  AND    (EC.SUBSIDIO <>0
   OR    EC.SOBREPRECIO<>0)
s$condicion$s
GROUP BY EC.SERVICIO
        ,S.NOMBRE
        ,EC.USO
        ,U.NOMBRE
        ,EC.ESTRATO
        ,E.NOMBRE
ORDER BY U.NOMBRE,E.NOMBRE
