SELECT DISTINCT 
	USUARIO.CODIGORUTA,
	USUARIO.PRIMERAPELLIDO || ' ' || USUARIO.SEGUNDOAPELLIDO || ' ' || USUARIO.NOMBRES AS NOMBREUSUARIO,
	USUARIO.CODIGOINTERNO,
	USUARIO.CICLO, 
	CASE  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s' 
	      THEN USUARIO.PESOASEO
	      ELSE USUARIO.PESOASEO * 0.25
	END AS TOTALPESO,
	USUARIO.USO, 
	USUARIO.ESTRATO,
	SUM(CASE FACTURADO.CONCEPTO WHEN 48 
		    					THEN FACTURADO.VALOR_FACTURADO
								ELSE 0 
		END) CN48,
	SUM(CASE FACTURADO.CONCEPTO WHEN 46 
								THEN FACTURADO.VALOR_FACTURADO
								ELSE 0 
		END) CN46,
    CASE s$productor$s  WHEN 1 
            THEN (CASE WHEN (CASE 
								  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
                                  THEN USUARIO.PESOASEO
                                  ELSE USUARIO.PESOASEO * 0.25
                             END) <= 0.25
                        THEN 'Pequeño Productor'
                        ELSE ' '
                  END)
            WHEN 2
            THEN (CASE WHEN (CASE
								WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
								THEN USUARIO.PESOASEO
								ELSE USUARIO.PESOASEO * 0.25
							END) > 0.25
                            AND 
						   (CASE
								WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
								THEN USUARIO.PESOASEO
								ELSE USUARIO.PESOASEO * 0.25
							END) < 1.5      
                       THEN 'Gran Productor Menos'
                       ELSE 'Gran Productor Mas'
                   END )
            WHEN 3
            THEN (CASE WHEN (CASE 
                                 WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
                                 THEN USUARIO.PESOASEO
                                 ELSE USUARIO.PESOASEO * 0.25
                              END) <= 0.25 
                       THEN 'Pequeño Productor'
                       WHEN ( CASE
								  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
								  THEN USUARIO.PESOASEO
								  ELSE USUARIO.PESOASEO * 0.25
                                  END) > 0.25
                            AND 
							( CASE
								  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
								  THEN USUARIO.PESOASEO
								  ELSE USUARIO.PESOASEO * 0.25
								END) < 1.5      
                        THEN 'Gran Productor Menos'
                        ELSE 'Gran Productor Mas'
                   END )
    END TIPOPRODUCTOR,
    USOS.NOMBRE
FROM SP_USUARIO USUARIO 
INNER JOIN SP_FACTURADO FACTURADO 
   ON USUARIO.COMPANIA   = FACTURADO.COMPANIA 
  AND USUARIO.CICLO      = FACTURADO.CICLO
  AND USUARIO.CODIGORUTA = FACTURADO.CODIGORUTA 
  AND USUARIO.ANO 		 = FACTURADO.ANO  
  AND USUARIO.PERIODO 	 = FACTURADO.PERIODO
INNER JOIN SP_USOS USOS 
   ON USUARIO.USO        = USOS.CODIGO 
  AND USUARIO.COMPANIA   = USOS.COMPANIA
WHERE 
s$condicionPesoAseo$s
  AND USUARIO.COMPANIA  = s$compania$s 
  AND USUARIO.CICLO     = s$condicionCiclo$s
  AND USUARIO.ESTADO    = 'A' 
  AND USUARIO.ASEO      = -1 
  AND USOS.NOMBRE NOT IN ('RESIDENCIAL')    
GROUP BY USUARIO.CODIGORUTA,
	USUARIO.PRIMERAPELLIDO || ' ' || USUARIO.SEGUNDOAPELLIDO || ' ' || USUARIO.NOMBRES ,
	USUARIO.CODIGOINTERNO,
	USUARIO.CICLO, 
	CASE WHEN 's$parToneladas$s' LIKE 's$parToneladas$s' 
		 THEN USUARIO.PESOASEO
		 ELSE USUARIO.PESOASEO * 0.25
	END,
	USUARIO.USO, 
	USUARIO.ESTRATO,
	CASE 3
	  WHEN 1 
	  THEN (CASE WHEN (CASE
					WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
					THEN USUARIO.PESOASEO
					ELSE USUARIO.PESOASEO * 0.25
				  END) <= 0.25
			  THEN 'Pequeño Productor'
			  ELSE ' '
		 END)
	  WHEN 2
		THEN (CASE WHEN (CASE
						WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
						THEN USUARIO.PESOASEO
						ELSE USUARIO.PESOASEO * 0.25
					END) > 0.25
					AND 
				   (CASE
						WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
						THEN USUARIO.PESOASEO
						ELSE USUARIO.PESOASEO * 0.25
					END) < 1.5      
			   THEN 'Gran Productor Menos'
			   ELSE 'Gran Productor Mas'
		  END )
	  WHEN 3
	  THEN ( CASE WHEN ( CASE 
				  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
						THEN USUARIO.PESOASEO
						ELSE USUARIO.PESOASEO * 0.25
					 END) <= 0.25 
			 THEN 'Pequeño Productor'
			   WHEN ( CASE
						  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
						  THEN USUARIO.PESOASEO
						  ELSE USUARIO.PESOASEO * 0.25
					  END) > 0.25
					AND 
					( CASE
						  WHEN 's$parToneladas$s' LIKE 's$parToneladas$s'
						  THEN USUARIO.PESOASEO
						  ELSE USUARIO.PESOASEO * 0.25
						END) < 1.5      
				THEN 'Gran Productor Menos'
				ELSE 'Gran Productor Mas'
				END )
	  END ,
	 USOS.NOMBRE 
ORDER BY USUARIO.CODIGORUTA
