SELECT 
 SP_USUARIO.CODIGORUTA,
  SP_USUARIO.ANO,
  SP_USUARIO.ESTADO,
  SP_USUARIO.PERIODO,
  SP_USUARIO.USO,
  SP_USUARIO.PRIMERAPELLIDO||' ' ||SP_USUARIO.SEGUNDOAPELLIDO||' '||SP_USUARIO.NOMBRES AS USUARIONOMBRES,
  SP_USUARIO.CODIGOINTERNO,
  SP_USUARIO.DIRTECNICA,
  SP_USUARIO.DIRGUIA,
  SP_USUARIO.DIRCORRESPONDENCIA,
  SP_USUARIO.MEDIDOR,
  SP_USUARIO.LECTURA1,
  SP_USUARIO.LECTURA,
  SP_USUARIO.LECTURA2,
  SP_USUARIO.PERIODOSACUMSINLECTURA,
  SP_USUARIO. PERIODOSINLECTURA,
  SP_USUARIO.LECTURAAFORO,
  SP_USUARIO.CONSUMO,

	CASE WHEN  SP_usuario.Consumo -(SP_usuario.ConsumoProm*(1+(s$limiteSuperior$s/100))) >=0 
       THEN 'Superior al promedio'
       ELSE 
          CASE WHEN  SP_usuario.Consumo -(SP_usuario.ConsumoProm*(1+(s$limiteInferior$s/100))) <=0
               THEN 'Inferior al promedio'
               ELSE  CASE WHEN (SP_usuario.Consumo>=500 AND Uso ='01') OR (SP_usuario.Consumo >=9000 AND Uso <>'01') 
                          THEN 'Elevado'
                          ELSE ''
                          END
               END
        END AS OBSERVACIONES,
  SP_usuario.CONSUMO1+SP_usuario.CONSUMO2+SP_usuario.CONSUMO3+SP_usuario.CONSUMO4+SP_usuario.CONSUMO5+SP_usuario.CONSUMO AS CnsAf ,
  SP_usuario.consumoprom AS caforo
FROM SP_usuario
WHERE (((SP_usuario.Compania)  = compania)
AND ((SP_usuario.Ciclo)        = s$ciclo$s)
AND ((SP_usuario.Consumo)     >= 60
AND ((SP_usuario.Consumo)     >= (ConsumoProm * (1 + (s$limiteSuperior$s / 100)))
OR (SP_usuario.Consumo)       <= (ConsumoProm * (1 + (s$limiteInferior$s / 100)))))
AND ((SP_usuario.ConsumoProm) >= 30))
OR (((SP_usuario.Compania)     =s$compania$s)
AND ((SP_usuario.Ciclo)        = s$ciclo$s)
AND ((SP_usuario.Consumo)     >= 500)
AND ((SP_usuario.Uso)          = '01'))
OR (((SP_usuario.Compania)     = s$compania$s)
AND ((SP_usuario.Ciclo)        = s$ciclo$s)
AND ((SP_usuario.Consumo)     >= 9000)
AND ((SP_usuario.Uso)          = '01'))
ORDER BY SP_Usuario.COMPANIA,
  SP_Usuario.CICLO,
  SP_Usuario.CODIGORUTA
