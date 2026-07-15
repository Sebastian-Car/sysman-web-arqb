 SELECT  DETALLE_COMPROBANTE_CNT.ANO                            VIGENCIA, 
        CASE WHEN TIPOID='C'
                THEN 'CC'
              ELSE 
         CASE WHEN TIPOID='N'
             THEN 'NIT'
            ELSE 
          CASE WHEN TIPOID = 'E'
              THEN 'CE'
            ELSE    
          CASE WHEN TIPOID ='T'
            THEN 'TI'
           END END END END                              TIPO_DOCUMENTO, 
          TERCERO.NIT                                   NRO_DOCUMENTO, 
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.NOMBRE1 
             END                                        PRIMER_NOMBRE,
         CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.NOMBRE2 
             END                                        OTROS_NOMBRES,
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.APELLIDO1   
             END                                        PRIMER_APELLIDO,
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.APELLIDO2   
             END                                        SEGUNDO_APELLIDO,
           CASE WHEN TIPOID  IN('N')
             THEN TERCERO.NOMBRE 
             END                                        RAZON_SOCIAL,
          TERCERO.DIRECCION                             DIRECCION_DE_NOTIFICACION, 
          TERCERO.TELEFONOS                             TELEFONO, 
          TERCERO.DIRECCIONEMAIL                        EMAIL, 
          CIUDAD.DEPARTAMENTO ||TERCERO.CIUDAD          COD_MUNICIPIO, 
          CIUDAD.DEPARTAMENTO                           COD_DEPTO, 
          SUM(DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE)    BASE_RETENCION, 
          PCT_APLICAR *10                              TARIFA_APLICADA, 
          SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)  MONTO_RETENCION_ANUAL 
		   FROM DETALLE_COMPROBANTE_CNT LEFT JOIN CONFIGURACION_EXOGENA
         ON  DETALLE_COMPROBANTE_CNT.COMPANIA = CONFIGURACION_EXOGENA.COMPANIA
			   AND DETALLE_COMPROBANTE_CNT.ANO = CONFIGURACION_EXOGENA.ANO 
			   AND DETALLE_COMPROBANTE_CNT.CUENTA = CONFIGURACION_EXOGENA.CODIGO 
		   LEFT JOIN RETENCIONES 
           ON  CONFIGURACION_EXOGENA.COMPANIA = RETENCIONES.COMPANIA
       	   AND CONFIGURACION_EXOGENA.ANO = RETENCIONES.ANO
           AND CONFIGURACION_EXOGENA.CUENTA = RETENCIONES.CUENTA_CREDITO
		   LEFT JOIN TERCERO 
            ON  DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA 
            AND DETALLE_COMPROBANTE_CNT.TERCERO = TERCERO.NIT 
            AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL 
		  LEFT JOIN CIUDAD 
                ON  TERCERO.PAIS = CIUDAD.PAIS 
                AND TERCERO.DEPARTAMENTO = CIUDAD.DEPARTAMENTO
                AND TERCERO.CIUDAD = CIUDAD.CODIGO
       WHERE DETALLE_COMPROBANTE_CNT.COMPANIA =s$compania$s
         AND DETALLE_COMPROBANTE_CNT.ANO =s$ano$s
         AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s    
         AND CONFIGURACION_EXOGENA.ID_SUJETO_RETENCION NOT IN(0)
		 AND CONFIGURACION_EXOGENA.FORMATO=s$formato$s
		 AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE<>'CIE' 
         GROUP BY DETALLE_COMPROBANTE_CNT.ANO, 
        CASE WHEN TIPOID='C'
                THEN 'CC'
              ELSE 
         CASE WHEN TIPOID='N'
             THEN 'NIT'
            ELSE 
          CASE WHEN TIPOID = 'E'
              THEN 'CE'
            ELSE    
          CASE WHEN TIPOID ='T'
            THEN 'TI'
           END END END END,                               
          TERCERO.NIT,                                    
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.NOMBRE1 
             END,
         CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.NOMBRE2 
             END,
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.APELLIDO1   
             END,
          CASE WHEN TIPOID NOT IN('N')
             THEN TERCERO.APELLIDO2   
             END,
           CASE WHEN TIPOID  IN('N')
             THEN TERCERO.NOMBRE 
             END ,                                 
          TERCERO.DIRECCION,                              
          TERCERO.TELEFONOS,                             
          TERCERO.DIRECCIONEMAIL,                         
          CIUDAD.DEPARTAMENTO ||TERCERO.CIUDAD,           
          CIUDAD.DEPARTAMENTO,                          
          PCT_APLICAR *10
           HAVING SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)>0
