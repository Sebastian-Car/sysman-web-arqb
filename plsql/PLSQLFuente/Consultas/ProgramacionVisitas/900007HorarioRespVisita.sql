WITH ULTIMA_VISITA AS 
                            (
                            SELECT 
                              VISITA.COMPANIA, 
                              VISITA.CLASE,
                              VISITA.CODIGO,
                              MAX(VISITA.NUMERO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTNROVISITA
                            FROM VISITA 
                            GROUP BY VISITA.COMPANIA, VISITA.CLASE, VISITA.CODIGO
                            ), ULTVISITA AS 
                            (SELECT 
                              U.COMPANIA, 
                              U.CLASE, 
                              U.CODIGO, 
                              U.ULTNROVISITA,
                              V.ESTADO,   
                              V.FECHA_INICIO,
                              V.FECHA_FIN 
                            FROM ULTIMA_VISITA U
                              INNER JOIN VISITA V
                                ON U.COMPANIA = V.COMPANIA
                                AND U.CLASE = V.CLASE
                                AND U.CODIGO = V.CODIGO
                                AND U.ULTNROVISITA = V.NUMERO)
                            SELECT 
                              R.CODIGO_VISITA, 
                              V.ULTNROVISITA   NUMERO_VISITA
                            FROM   RESPONSABLE_VISITA R 
                               INNER JOIN ULTVISITA  V
                               ON  R.COMPANIA = V.COMPANIA 
                               AND  R.CLASE_VISITA = V.CLASE 
                               AND  R.CODIGO_VISITA = V.CODIGO 
                               AND  R.NUMERO_VISITA = V.ULTNROVISITA  
                                INNER JOIN ESTADO_VISITA E
                                ON V.COMPANIA = E.COMPANIA
                                AND V.ESTADO = E.CODIGO 
                            WHERE  R.COMPANIA           =  s$compania$s
                              AND  R.CODIGO_VISITA      <> s$codVisita$s
                              AND  R.RESPONSABLE        = 's$responsable$s'
                              AND  R.SUCURSAL           = 's$sucResponsable$s'
                              AND  ( TO_DATE(V.FECHA_INICIO  , 'DD/MM/YYYY HH24:MI:SS')    < TO_DATE('s$fechaIni$s' , 'DD/MM/YYYY HH24:MI:SS')
                              AND   TO_DATE(V.FECHA_FIN , 'DD/MM/YYYY HH24:MI:SS')          > TO_DATE('s$fechaIni$s' , 'DD/MM/YYYY HH24:MI:SS')) 
                               OR  ( TO_DATE(V.FECHA_INICIO   , 'DD/MM/YYYY HH24:MI:SS')   < TO_DATE('s$fechaFin$s' , 'DD/MM/YYYY HH24:MI:SS') 
                              AND   TO_DATE(V.FECHA_FIN    , 'DD/MM/YYYY HH24:MI:SS')       > TO_DATE('s$fechaFin$s' , 'DD/MM/YYYY HH24:MI:SS'))
