SELECT   COMPANIA
       , CODIGORUTA
       , ANO
       , ESTADO
       , PERIODO
       , USO
       , PRIMERAPELLIDO||' '||SEGUNDOAPELLIDO||' '||NOMBRES
 NOMBRES
       , CODIGOINTERNO
       , DIRTECNICA
       , DIRGUIA
       , DIRCORRESPONDENCIA
       , MEDIDOR
       , LECTURA1
       , LECTURA
       , LECTURA2
       , PERIODOSINLECTURA
       , LECTURAAFORO
       , CONSUMO CNSAF
       , CONSUMO
       , CONSUMOPROM CAFORO
       , MEDIDOR
       , CASE WHEN CONSUMOPROM< s$txtCnsMenor$s
              THEN CASE WHEN CONSUMO>((CONSUMO1
       	                              +CONSUMO2
       	                              +CONSUMO3
       	                              +CONSUMO4
       	                              +CONSUMO5
       	                              +CONSUMO)/6)
                                     *(1 + s$txtPorMenor$s / 100)
	                    THEN 'Superior al Promedio'
	                    ELSE CASE WHEN CONSUMO<((CONSUMO1
       	                                        +CONSUMO2
       	                                        +CONSUMO3
       	                                        +CONSUMO4
       	                                        +CONSUMO5
       	                                        +CONSUMO)/6)
                                               *(1 - s$txtPorMenor$s / 100 )
	                              THEN 'Inferior al Promedio'
	                              ELSE 'Normal'
	                         END
	               END
	          ELSE CASE WHEN CONSUMOPROM>= s$txtCnsMayor$s 
	                    THEN CASE WHEN CONSUMO>((CONSUMO1
       	                                        +CONSUMO2
       	                                        +CONSUMO3
       	                                        +CONSUMO4
       	                                        +CONSUMO5
       	                                        +CONSUMO)/6)
                                               *( 1 + s$txtPorMayor$s / 100 )
	                              THEN 'Superior al Promedio'
	                              ELSE CASE WHEN CONSUMO<((CONSUMO1
       	                                                  +CONSUMO2
       	                                                  +CONSUMO3
       	                                                  +CONSUMO4
       	                                                  +CONSUMO5
       	                                                  +CONSUMO)/6)
                                                         *( 1 - s$txtPorMayor$s / 100 )
	                                        THEN 'Inferior al Promedio'
	                                        ELSE 'Normal'
	                                   END
	                         END
	                    ELSE 'Normal'
	               END
	     END OBSERVACIONES
       , CICLO 
  FROM SP_USUARIO
 WHERE COMPANIA   = s$compania$s 
   AND Ciclo      = s$ciclo$s
   AND CODIGORUTA BETWEEN 's$codigoInicial$s' AND 's$codigoFinal$s' 
   AND ESTADO     IN ('A') 
   AND CONSUMO    >=  s$consumo$s   
   AND UPPER(CASE WHEN CONSUMOPROM< s$txtCnsMenor$s
              THEN CASE WHEN CONSUMO>((CONSUMO1
       	                              +CONSUMO2
       	                              +CONSUMO3
       	                              +CONSUMO4
       	                              +CONSUMO5
       	                              +CONSUMO)/6)
                                     *(1 + s$txtPorMenor$s / 100)
	                    THEN 'Superior al Promedio'
	                    ELSE CASE WHEN CONSUMO<((CONSUMO1
       	                                        +CONSUMO2
       	                                        +CONSUMO3
       	                                        +CONSUMO4
       	                                        +CONSUMO5
       	                                        +CONSUMO)/6)
                                               *(1 - s$txtPorMenor$s / 100 )
	                              THEN 'Inferior al Promedio'
	                              ELSE 'Normal'
	                         END
	               END
	          ELSE CASE WHEN CONSUMOPROM>= s$txtCnsMayor$s 
	                    THEN CASE WHEN CONSUMO>((CONSUMO1
       	                                        +CONSUMO2
       	                                        +CONSUMO3
       	                                        +CONSUMO4
       	                                        +CONSUMO5
       	                                        +CONSUMO)/6)
                                               *( 1 + s$txtPorMayor$s / 100 )
	                              THEN 'Superior al Promedio'
	                              ELSE CASE WHEN CONSUMO<((CONSUMO1
       	                                                  +CONSUMO2
       	                                                  +CONSUMO3
       	                                                  +CONSUMO4
       	                                                  +CONSUMO5
       	                                                  +CONSUMO)/6)
                                                         *( 1 - s$txtPorMayor$s / 100 )
	                                        THEN 'Inferior al Promedio'
	                                        ELSE 'Normal'
	                                   END
	                         END
	                    ELSE 'Normal'
	               END
	     END)= UPPER('s$observaciones$s') 
