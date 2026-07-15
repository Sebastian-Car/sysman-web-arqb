SELECT  CONSORCIADOS.NITCONSORCIO                             "Nit Del Consorcio/ Unión Temporal",
        CONSORCIADOS.NOMBRE                                   "Nombre Del Consorcio/ Unión Temporal",
        CONSORCIADOS.NIT                                      "Cédula/ Nit  Integrantes  Consorcio O UT",
        TERCERO.NOMBRE                                        "Nombre Integrantes Consorcio O UT",
        COMPANIA.NITCOMPANIA                                  "Nit Del Sujeto Vigilado",
        ORDENDECOMPRA.CLASEORDEN||'-'||ORDENDECOMPRA.NUMERO   "Número Del Contrato"
FROM TERCERO INNER JOIN CONSORCIADOS
         ON TERCERO.COMPANIA        = CONSORCIADOS.COMPANIA
         AND TERCERO.NIT            = CONSORCIADOS.NIT
         AND TERCERO.SUCURSAL       = CONSORCIADOS.SUCURSAL
    INNER JOIN COMPANIA 
       ON TERCERO.COMPANIA          =COMPANIA.CODIGO
    INNER JOIN ORDENDECOMPRA    
         ON TERCERO.COMPANIA        = ORDENDECOMPRA.COMPANIA
         AND TERCERO.NIT            = ORDENDECOMPRA.TERCERO
         AND TERCERO.SUCURSAL       = ORDENDECOMPRA.SUCURSAL
 WHERE TERCERO.COMPANIA=s$compania$s
       AND  TO_CHAR(OC.FECHA,'YYYY')=s$ano$s
       AND  TO_NUMBER(TO_CHAR(OC.FECHA,'MM')) BETWEEN s$mesInicial$s  AND s$mesFinal$s;
       AND TERCERO.TIPO_CONTRATISTA IN(2,3)
