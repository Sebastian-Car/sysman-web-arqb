 SELECT BASECONSULTA.COMPANIA
      , BASECONSULTA.ANO
      , DETALLE.MES
      , BASECONSULTA.CODIGO
       , DETALLE.ID 
      , BASECONSULTA.NOMBRE PNOMBRE
      , SUBSTR(BASECONSULTA.CODIGO, 1,1) CLASE
      , BASECONSULTA.SALDO1 SALDO
      , BASECONSULTA.SALDOs$mesAnterior$s SALDO_1
      , BASECONSULTA.DEBITOs$mesActual$s DEBITO
      , BASECONSULTA.CREDITOs$mesActual$s CREDITO
      , BASECONSULTA.DEBITOs$mesAnterior$s DEBITO_1
      , BASECONSULTA.CREDITOs$mesAnterior$s CREDITO_1
      , BASECONSULTA.TERCERO NITCOMPLETO
      , BASECONSULTA.CENTRO_COSTO	
      , BASECONSULTA.AUXILIAR
      , BASECONSULTA.REFERENCIA
      , BASECONSULTA.FUENTE_RECURSOS
      , (CASE WHEN BASECONSULTA.NATURALEZA = 'D' AND BASECONSULTA.SALDOs$mesAnterior$s >= 0 
                                THEN  BASECONSULTA.SALDOs$mesAnterior$s
                                ELSE 0 
                     END) +
        (CASE WHEN BASECONSULTA.NATURALEZA = 'C' AND BASECONSULTA.SALDOs$mesAnterior$s  < 0 
                                THEN -BASECONSULTA.SALDOs$mesAnterior$s
                                ELSE 0 
                     END) SALDOANTDEBITO
      , (CASE WHEN BASECONSULTA.NATURALEZA = 'C' AND BASECONSULTA.SALDOs$mesAnterior$s >= 0
                                THEN  BASECONSULTA.SALDOs$mesAnterior$s  
                                ELSE 0 
                      END) +
        (CASE WHEN BASECONSULTA.NATURALEZA = 'D' AND BASECONSULTA.SALDOs$mesAnterior$s  < 0  
                                 THEN -BASECONSULTA.SALDOs$mesAnterior$s
                                 ELSE 0 
                      END) SALDOANTCREDITO
      , (CASE WHEN BASECONSULTA.NATURALEZA = 'D' AND BASECONSULTA.SALDOs$mesActual$s >= 0   
                                THEN  BASECONSULTA.SALDOs$mesActual$s  
                                ELSE 0 
                     END) +
                     (CASE WHEN BASECONSULTA.NATURALEZA = 'C' AND BASECONSULTA.SALDOs$mesActual$s  < 0   
                               THEN -BASECONSULTA.SALDOs$mesActual$s   
                               ELSE 0 
                      END) SALDONUEDEBITO
	    , (CASE WHEN BASECONSULTA.NATURALEZA = 'C' AND BASECONSULTA.SALDOs$mesActual$s >= 0   
                                THEN  BASECONSULTA.SALDOs$mesActual$s
                                ELSE 0 
                       END) +
                       (CASE WHEN BASECONSULTA.NATURALEZA = 'D' AND BASECONSULTA.SALDOs$mesActual$s  < 0   
                                  THEN -BASECONSULTA.SALDOs$mesActual$s   
                                  ELSE 0 
                       END) SALDONUECREDITO
      , DETALLE.FECHA
      , DETALLE.TIPO_CPTE
      , DETALLE.COMPROBANTE
      , DETALLE.NRO_DOCUMENTO
      , DETALLE.VALOR_DEBITO
      , DETALLE.VALOR_CREDITO
      , DETALLE.DESCRIPCION
      , DETALLE.TNOMBRE
 T1NOMBRE
      , SUBSTR((CASE WHEN SUBSTR(BASECONSULTA.CODIGO,1,1) = '0'  
                   THEN 'Z'||BASECONSULTA.CODIGO 
                   ELSE BASECONSULTA.CODIGO 
          END),1,1) CLASEORDEN
      , CASE WHEN SUBSTR(BASECONSULTA.CODIGO,1,1) = '0'  
                                 THEN 'Z'||BASECONSULTA.CODIGO 
                                 ELSE BASECONSULTA.CODIGO 
                        END ORDEN
      ,  (BASECONSULTA.CODIGO||(CASE WHEN BASECONSULTA.CENTRO_COSTO IS NULL 
                                                                                   THEN ' '
                                                                                   ELSE '' 
                                                                         END)||(CASE WHEN BASECONSULTA.TERCERO IS NULL 
                                                                                               THEN ' ' 
                                                                                               ELSE '' 
                                                                                     END)||(CASE WHEN BASECONSULTA.AUXILIAR 	   IS NULL 
                                                                                                           THEN ' ' 
                           				ELSE '' 
				             END)|| BASECONSULTA.NOMBRE) ORDENCUENTAS
     , DETALLE.TCNOMBRE
 FROM  
 (
s$baseBalance$s) BASECONSULTA INNER JOIN 
 (SELECT D.COMPANIA
      , D.ANO
      , D.MES
      , D.CUENTA 
      , TO_CHAR(TRUNC(D.FECHA), 'DD/MM/YYYY') FECHA
      , D.TIPO_CPTE
      , D.COMPROBANTE
      , D.NRO_DOCUMENTO
      , D.VALOR_DEBITO
      , D.VALOR_CREDITO
      , D.DESCRIPCION
      , T.NOMBRE TNOMBRE
      , C.NOMBRE TCNOMBRE
      , D.TERCERO
      , D.AUXILIAR
      , D.CENTRO_COSTO
      , D.REFERENCIA
      , D.FUENTE_RECURSO
      , D.ID
 FROM DETALLE_COMPROBANTE_CNT D
 INNER JOIN TERCERO T
 ON D.COMPANIA = T.COMPANIA
 AND D.TERCERO = T.NIT
 AND D.SUCURSAL = T.SUCURSAL
 INNER JOIN TIPO_COMPROBANTE C
 ON D.COMPANIA = C.COMPANIA
 AND D.TIPO_CPTE = C.CODIGO
 WHERE D.COMPANIA = s$compania$s
 AND D.ANO = s$anio$s
 AND D.MES = s$mesActual$s
 AND D.TERCERO BETWEEN 's$terceroInicial$s' AND 's$terceroFinal$s') DETALLE
 ON BASECONSULTA.COMPANIA = DETALLE.COMPANIA
 AND BASECONSULTA.ANO = DETALLE.ANO
 AND BASECONSULTA.CODIGO = DETALLE.CUENTA
s$filtro$s 
