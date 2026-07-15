UNION
      
SELECT TO_NUMBER('') APRINICIAL,
       '' MES, 
       '' DIA,
       TO_CHAR(ANO) ANO,
       '' COMPROBANTE,
       '' TIPO_CPTE,
       '' CLASE,
       TO_NUMBER('') VALORTOTAL,
       '' SIGNO,
       TO_NUMBER('') VALOR,
       '' CLASECOMP, 
       '.' CLASELETRAS,     
       SUM(TOTALREO) CXPCONSTITUIDAS,
       TO_NUMBER('') INICIAL,
       TO_NUMBER('') CANCELACION,
       TO_NUMBER('') EJECUCION, 
       TO_NUMBER('') REVERSION,
       SUM(EJECUCIONPPT - REINTEGRO) TOTALPAGOS,
       '' DESCRIPCION, 
       NOMBRE, 
       '' NIVEL1,
       '' NIVEL2,
       '' NIVEL3,
       '' NIVEL4,
       '' NIVEL5,
       '' NIVEL6,
       '' NIVEL7,
       ID,
       '' CODIGO, 
       '' NATURALEZA, 
       '' FECHA,
       CASE DESTINO 
            WHEN 'F' THEN 'Funcionamiento'
            WHEN 'I' THEN 'Inversión'
            WHEN 'S' THEN 'Servicio de la Deuda'
       END DESTINO,
       '' NRO_DOCUMENTO, 
       '' TIPOVIGENCIA,
       '' NOMBRETERCERO,
       TO_NUMBER('') CUENTASXPAGAR
 FROM V_RESUMENPPTO_BASE
 WHERE COMPANIA = s$compania$s
   AND ANO      = s$anio$s
   AND ID BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
   AND MES BETWEEN s$mesInicial$s AND s$mesFinal$s
   AND LENGTH(ID) <=s$nivel$s   
   AND MOVIMIENTO NOT IN(0)
   AND NATURALEZA ='D'
 GROUP BY COMPANIA, 
		   ANO, 
       MES,
		   ID, 
		   CODIGO,
		   NATURALEZA,
		   CENTRO_COSTO,
		   TERCERO,
		   SUCURSAL,
		   AUXILIAR,
		   REFERENCIA,  
		   FUENTE_RECURSO,
		   MOVIMIENTO,
       NOMBRE, 
       NIVEL1,
       NIVEL2,
       NIVEL3,
       NIVEL4,
       NIVEL5,
       NIVEL6, 
       DESTINO
