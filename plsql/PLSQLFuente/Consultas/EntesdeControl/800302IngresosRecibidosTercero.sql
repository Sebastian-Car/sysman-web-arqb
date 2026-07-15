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
          PAISES.NOMBRE                                 PAIS_RESIDENCIA,
          CONFIGURACION_EXOGENA.CONCEPTO                CONCEPTO_PAGO,
          SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - VALOR_DEBITO) VALOR_DEL_INGRESO
       FROM DETALLE_COMPROBANTE_CNT LEFT JOIN CONFIGURACION_EXOGENA
         ON  DETALLE_COMPROBANTE_CNT.COMPANIA = CONFIGURACION_EXOGENA.COMPANIA
			   AND DETALLE_COMPROBANTE_CNT.ANO = CONFIGURACION_EXOGENA.ANO 
			   AND DETALLE_COMPROBANTE_CNT.CUENTA = CONFIGURACION_EXOGENA.CUENTA 
	   LEFT JOIN TERCERO 
            ON  DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA 
            AND DETALLE_COMPROBANTE_CNT.TERCERO =  TERCERO.NIT 
            AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL 
		  LEFT JOIN CIUDAD 
                ON  TERCERO.PAIS = CIUDAD.PAIS 
                AND TERCERO.DEPARTAMENTO = CIUDAD.DEPARTAMENTO
                AND TERCERO.CIUDAD = CIUDAD.CODIGO
        LEFT JOIN PAISES 
                ON TERCERO.PAIS = PAISES.PAIS 
       WHERE DETALLE_COMPROBANTE_CNT.COMPANIA =s$compania$s
         AND DETALLE_COMPROBANTE_CNT.ANO =s$ano$s
         AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s    
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
          PAISES.NOMBRE,
          CONFIGURACION_EXOGENA.CONCEPTO
        HAVING SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - VALOR_DEBITO) >0
