SELECT CONFIGURACION_EXOGENA.CONCEPTO                          CONCEPTO,
       TIPOS_DOCUMENTOS.CODIGODIAN                           TIPO_DE_IDENTIFICACIÓN,
       DETALLE_COMPROBANTE_CNT.TERCERO                       NUMERO_IDENTIFICACIÓN,
       CASE WHEN TERCERO.TIPOID='C'
          THEN TERCERO.APELLIDO1
         END                                                  PRIMER_APELLIDO_DEL_INFORMADO,
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.APELLIDO2
         END                                                 SEGUNDO_APELLIDO_DEL_INFORMADO,
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.NOMBRE1
         END                                                  PRIMER_NOMBRE_DEL_INFORMADO,
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.NOMBRE2
         END                                                  OTROS_NOMBRES_DEL_INFORMADO,
       CASE WHEN TERCERO.TIPOID='N'
               THEN TERCERO.NOMBRE
         END                                                  RAZON_SOCIAL_INFORMADO,
      TERCERO.DIRECCION                                       DIRECCION,
      TERCERO.DEPARTAMENTO                                    CÓDIGO_DEPARTAMENTO,
      TERCERO.CIUDAD                                          CÓDIGO_MUNICIPIO,
      PAISES.CODIGODIAN                                       PAIS_DE_RESIDENCIA_O_DOMICILIO,
      TERCERO.DIRECCIONEMAIL                                  CORREO_ELECTRÓNICO,
      SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-
            VALOR_CREDITO)                                    VALOR_SUJETO_RETENCION,
     0                                                        VALOR_DESCUENTO_TRIBUTARIO        
  FROM DETALLE_COMPROBANTE_CNT  INNER JOIN CONFIGURACION_EXOGENA
        ON  DETALLE_COMPROBANTE_CNT.COMPANIA=CONFIGURACION_EXOGENA.COMPANIA
        AND DETALLE_COMPROBANTE_CNT.ANO=CONFIGURACION_EXOGENA.ANO
        AND DETALLE_COMPROBANTE_CNT.CUENTA=CONFIGURACION_EXOGENA.CUENTA
     INNER JOIN TERCERO
           ON  DETALLE_COMPROBANTE_CNT.COMPANIA=TERCERO.COMPANIA
           AND DETALLE_COMPROBANTE_CNT.TERCERO=TERCERO.NIT
           AND DETALLE_COMPROBANTE_CNT.SUCURSAL=TERCERO.SUCURSAL
      LEFT JOIN TIPOS_DOCUMENTOS
           ON   TERCERO.COMPANIA=TIPOS_DOCUMENTOS.COMPANIA
           AND  TERCERO.TIPOID  =TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
     LEFT JOIN PAISES
            ON TERCERO.PAIS=PAISES.PAIS      
WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
      AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
      AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
      AND CONFIGURACION_EXOGENA.FORMATO=s$formato$s
      AND CONFIGURACION_EXOGENA.CONCEPTO IS NOT NULL
GROUP BY  CONFIGURACION_EXOGENA.CONCEPTO,                          
       TIPOS_DOCUMENTOS.CODIGODIAN,                           
       DETALLE_COMPROBANTE_CNT.TERCERO,                       
       TERCERO.DIGITOVERIFICACION,                            
        CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.APELLIDO1
         END,                                                  
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.APELLIDO2
         END,                                                 
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.NOMBRE1
         END,                                                  
       CASE WHEN TERCERO.TIPOID='C'
               THEN TERCERO.NOMBRE2
         END,                                                 
       CASE WHEN TERCERO.TIPOID='N'
               THEN TERCERO.NOMBRE
         END,                                                      
      TERCERO.DIRECCION,                                      
      TERCERO.DEPARTAMENTO,                                    
      TERCERO.CIUDAD,
      PAISES.CODIGODIAN,                                       
      TERCERO.DIRECCIONEMAIL
